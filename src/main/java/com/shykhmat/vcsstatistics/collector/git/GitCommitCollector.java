package com.shykhmat.vcsstatistics.collector.git;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import com.shykhmat.vcsstatistics.collector.VCSStatisticsCollectorException;

/**
 * Class to collect all commits from repository.
 */
public class GitCommitCollector {

    /**
     * Method to retrieve commits from repository
     * 
     * @param repository
     *            - Git repository
     * @return {@link Stream} with commits
     * @throws VCSStatisticsCollectorException
     *             if any error occurred
     */
    public Stream<RevCommit> getCommitStream(Repository repository) throws VCSStatisticsCollectorException {
        try (RevWalk walker = getConfiguredWalker(repository)) {
            return StreamSupport.stream(walker.spliterator(), false);
        }
    }

    private RevWalk getConfiguredWalker(Repository repository) throws VCSStatisticsCollectorException {
        try {
            RevWalk walker = new RevWalk(repository);
            walker.setRevFilter(RevFilter.NO_MERGES);
            ObjectId head = repository.resolve(Constants.HEAD);
            if (head == null) {
                throw new VCSStatisticsCollectorException("Specified project folder is not a Git repository");
            }
            walker.markStart(walker.parseCommit(head));
            return walker;
        } catch (IOException e) {
            throw new VCSStatisticsCollectorException(e);
        }
    }
}
