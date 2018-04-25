package com.shykhmat.vcsstatistics.collector.git;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.MyersDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to analyze commits and find differences.
 */
public class GitCommitChangesEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitCommitChangesEvaluator.class);

    /**
     * Method to calculate amount of changed lines in commit.
     * 
     * @param repository
     *            - Git repository that contains commit
     * @param commit
     *            - changes to analyze
     * @return amount of changed lines in commit
     */
    public long evaluateChangedLines(Repository repository, RevCommit commit) {
        return (long) obtainDiffs(repository, commit).stream().flatMap(diff -> obtainModifiedRegions(repository, diff)).collect(Collectors.summarizingLong(this::getInsertions)).getSum();
    }

    private List<DiffEntry> obtainDiffs(Repository repository, RevCommit commit) {
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            return diffFormatter.scan(getParent(commit), commit);
        } catch (IOException e) {
            LOGGER.error("Cannot obtain diffs for commit {} because of {}", commit.getId(), e);
        }
        return Collections.emptyList();
    }

    private RevCommit getParent(RevCommit commit) {
        return (commit.getParentCount() > 0) ? commit.getParent(0) : null;
    }

    private Stream<Edit> obtainModifiedRegions(Repository repository, DiffEntry diff) {
        RawText oldRawText = getRawText(repository, diff.getOldId().toObjectId());
        RawText newRawText = getRawText(repository, diff.getNewId().toObjectId());
        return MyersDiff.INSTANCE.diff(RawTextComparator.DEFAULT, oldRawText, newRawText).stream();
    }

    private RawText getRawText(Repository repository, ObjectId id) {
        try {
            return getNonBinaryRawText(repository, id);
        } catch (IOException e) {
            return RawText.EMPTY_TEXT;
        }
    }

    private RawText getNonBinaryRawText(Repository repository, ObjectId id) throws IOException {
        byte[] bytes = repository.open(id).getBytes();
        if (!RawText.isBinary(bytes)) {
            return new RawText(bytes);
        }
        return RawText.EMPTY_TEXT;
    }

    private int getInsertions(Edit modifiedRegion) {
        return modifiedRegion.getLengthB();
    }
}
