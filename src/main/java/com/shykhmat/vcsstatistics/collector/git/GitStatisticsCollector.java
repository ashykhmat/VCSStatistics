package com.shykhmat.vcsstatistics.collector.git;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shykhmat.vcsstatistics.collector.VCSStatisticsCollector;
import com.shykhmat.vcsstatistics.collector.VCSStatisticsCollectorException;
import com.shykhmat.vcsstatistics.domain.ProjectReport;
import com.shykhmat.vcsstatistics.utils.DateUtils;

/**
 * Implementation of {@link VCSStatisticsCollector} to work with Git version
 * control system.
 */
public class GitStatisticsCollector implements VCSStatisticsCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitStatisticsCollector.class);
    private static final String GIT_PROJECT_CONFIG_FOLDER = ".git";

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectReport collectStatistics(String projectPath, LocalDate dateFrom, LocalDate dateTo) throws VCSStatisticsCollectorException {
        LOGGER.info("Obtaining Git repository at path {}", projectPath);
        String fixedProjectPath = fixProjectPath(projectPath);
        try (Repository repository = new FileRepositoryBuilder().setGitDir(new File(fixedProjectPath)).build()) {
            Map<String, Map<LocalDate, Long>> userReport = new HashMap<>();
            GitCommitCollector commitCollector = new GitCommitCollector();
            LOGGER.info("Collecting commits");
            List<RevCommit> commits = commitCollector.getCommitStream(repository).sorted((c1, c2) -> getCommitDate(c1).compareTo(getCommitDate(c2))).collect(Collectors.toList());
            if (commits.size() > 0) {
                dateFrom = dateFrom == null ? getCommitDate(commits.get(0)) : dateFrom;
                LOGGER.info("First processing commit date {}", dateFrom.toString());
                dateTo = dateTo == null ? getCommitDate(commits.get(commits.size() - 1)) : dateTo;
                LOGGER.info("Last processing commit date {}", dateTo.toString());
                LOGGER.info("Collecting commit statistics");
                GitCommitChangesEvaluator commitChangesEvaluator = new GitCommitChangesEvaluator();
                userReport = commits.stream().filter(constructCommitsDateRangeFilter(dateFrom, dateTo)).collect(
                                Collectors.groupingBy(this::getAuthorName, Collectors.groupingBy(this::getCommitDate,
                                        Collectors.summingLong(commit -> commitChangesEvaluator.evaluateChangedLines(repository, commit)))));
            }
            return new ProjectReport(obtainRepositoryName(repository), dateFrom, dateTo, userReport);
        } catch (IOException e) {
            throw new VCSStatisticsCollectorException(e);
        }
    }

    private String fixProjectPath(String projectPath) {
        String fixedProjectPath = projectPath;
        if (!fixedProjectPath.endsWith(GIT_PROJECT_CONFIG_FOLDER)) {
            if (!fixedProjectPath.endsWith(File.separator)) {
                fixedProjectPath += File.separator;
            }
            fixedProjectPath += GIT_PROJECT_CONFIG_FOLDER;
        }
        return fixedProjectPath;
    }

    private LocalDate getCommitDate(RevCommit commit) {
        return DateUtils.toLocalDate(commit.getCommitTime());
    }

    public String getAuthorName(RevCommit commit) {
        PersonIdent person = commit.getAuthorIdent() != null ? commit.getAuthorIdent() : commit.getCommitterIdent();
        return person.getName();
    }

    private Predicate<RevCommit> constructCommitsDateRangeFilter(LocalDate dateFrom, LocalDate dateTo) {
        Predicate<RevCommit> dateFromPredicate = commit -> !getCommitDate(commit).isBefore(dateFrom);
        Predicate<RevCommit> dateToPredicate = commit -> !getCommitDate(commit).isAfter(dateTo);
        return dateFromPredicate.and(dateToPredicate);
    }

    private String obtainRepositoryName(Repository repository) {
        return getRemoteRepoURL(repository).map(this::getRepoNameFromUrl).orElse(getRepoDirName(repository)).replace(GIT_PROJECT_CONFIG_FOLDER, "");
    }

    private Optional<String> getRemoteRepoURL(Repository repository) {
        return Optional.ofNullable(repository.getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "url"));
    }

    private String getRepoNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/"));
    }

    private String getRepoDirName(Repository repository) {
        return repository.getWorkTree().getName();
    }

}
