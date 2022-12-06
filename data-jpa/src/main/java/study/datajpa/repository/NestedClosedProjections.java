package study.datajpa.repository;

/**
 * Projections - Member + Team
 */
public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
