package ru.aston.hw3.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.developer.DeveloperRepository;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.owner.OwnerRepository;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.solution.SolutionRepository;
import ru.aston.hw3.solution.model.Solution;

import java.sql.*;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JDBCPostgres implements OwnerRepository, SolutionRepository, DeveloperRepository {
    private final String driverClassName;
    private final String dbUrl;
    private final String username;
    private final String password;

    public JDBCPostgres() {
        driverClassName = "org.postgresql.Driver";
        dbUrl = "jdbc:postgresql://localhost:5432/db_aston_hw3";
        username = "aston";
        password = "aston_pwd";
    }

    @Override
    public Optional<Owner> addOwner(Owner newOwner) {
        Optional<Owner> ownerOptional;
        String sql = "INSERT INTO owners (last_name, first_name, email) VALUES (?, ?, ?);";
        long resultId = -1;
        log.info("OR: add Owner={} ", newOwner);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, newOwner.getFirstName());
            ps.setString(2, newOwner.getLastName());
            ps.setString(3, newOwner.getEmail());
            ps.executeUpdate();
            if (ps.getGeneratedKeys().next()) {
                resultId = ps.getGeneratedKeys().getLong(1);
                newOwner.setId(resultId);
            }
            ownerOptional = Optional.of(newOwner);
        } catch (SQLException e) {
            log.error("OR: addOwner SQLException");
            e.printStackTrace();
            ownerOptional = Optional.empty();
        }
        log.info("OR: add Owner={}, newId={} ", ownerOptional, resultId);
        return ownerOptional;
    }

    @Override
    public Optional<Owner> getOwner(long id, boolean isLazy) {
        Optional<Owner> ownerOptional = Optional.empty();
        String sql;
        if (isLazy) {
            sql = "SELECT * " +
                    "FROM owners o " +
                    "WHERE id = ?";
        } else {
            sql = "SELECT *" +
                    "FROM owners o " +
                    "LEFT JOIN solutions s on o.id = s.owner_id " +
                    "WHERE o.id = ?";
        }
        log.info("OR: get Owner by id={} ", id);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    log.debug(i + ":" + rsmd.getColumnName(i));
                }
                Owner owner = Owner.builder().build();
                List<Solution> solutionList = new ArrayList<>(Collections.emptyList());
                while (rs.next()) {
                    if (owner.getId() == null) {
                        owner.setId(rs.getLong(1));
                        owner.setFirstName(rs.getString(2));
                        owner.setLastName(rs.getString(3));
                        owner.setEmail(rs.getString(4));
                    }
                    if (!isLazy && (rs.getObject(5) != null)) {
                        solutionList.add(Solution.builder()
                                .id(rs.getLong(5))
                                .name(rs.getString(6))
                                .version(rs.getString(7))
                                .build()
                        );

                    }
                    ownerOptional = Optional.of(owner);
                }
                if (!isLazy) {
                    owner.setSolutions(solutionList);
                }
            }
        } catch (SQLException e) {
            log.error("OR: getOwner SQLException");
            e.printStackTrace();
            ownerOptional = Optional.empty();
        }
        return ownerOptional;
    }

    @Override
    public int deleteOwner(long id) {
        String sql = "DELETE FROM owners WHERE id = ?";

        int numOfDeleted = 0;
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            numOfDeleted = ps.executeUpdate();
        } catch (SQLException e) {
            log.error("OR: getOwner SQLException");
            e.printStackTrace();
        }
        log.info("OR: delete Owner by id={}, num of deleted={} ", id, numOfDeleted);
        return numOfDeleted;
    }

    @Override
    public Optional<Solution> addSolution(Solution newSolution) {
        Optional<Solution> solutionOptional = Optional.empty();
        String sql = "INSERT INTO solutions (name, version, owner_id) VALUES (?, ?, ?);";
        long resultId = -1;
        log.info("OR: add Solution={} ", newSolution);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, newSolution.getName());
            ps.setString(2, newSolution.getVersion());
            if (newSolution.getOwner() != null) {
                ps.setLong(3, newSolution.getOwner().getId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.executeUpdate();
            if (ps.getGeneratedKeys().next()) {
                resultId = ps.getGeneratedKeys().getLong(1);
                newSolution.setId(resultId);
                solutionOptional = Optional.of(newSolution);
            }
        } catch (SQLException e) {
            log.error("OR: addSolution SQLException");
            e.printStackTrace();
        }
        log.info("OR: add Solution={}, newId={} ", newSolution, resultId);

        return solutionOptional;
    }

    @Override
    public Optional<Solution> getSolution(long id, boolean isLazy) {
        Optional<Solution> optionalSolution = Optional.empty();
        String sql;
        if (isLazy) {
            sql = "SELECT * " +
                    "FROM solutions " +
                    "WHERE id = ?";
        } else {
            sql = "SELECT * " +
                    "FROM solutions s " +
                    "LEFT JOIN owners o on owner_id = o.id " +
                    "LEFT JOIN solutions_developers sd on s.id = sd.solutions_id " +
                    "LEFT JOIN developers d on sd.developer_id = d.id " +
                    "WHERE s.id = ?";
        }

        log.info("OR: get Solution by id={} ", id);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    log.debug(i + ":" + rsmd.getColumnName(i));
                }
                Solution solution = Solution.builder().build();
                List<Developer> developerList = new ArrayList<>(Collections.emptyList());
                while (rs.next()) {
                    if (solution.getId() == null) {
                        solution.setId(rs.getLong(1));
                        solution.setName(rs.getString(2));
                        solution.setVersion(rs.getString(3));
                    }
                    if (!isLazy) {
                        if (solution.getOwner() == null) {
                            solution.setOwner(Owner.builder()
                                    .id(rs.getLong(5))
                                    .lastName(rs.getString(6))
                                    .firstName(rs.getString(7))
                                    .email(rs.getString(8))
                                    .build());
                        }
                        if (rs.getObject(12) != null) {
                            developerList.add(Developer.builder()
                                    .id(rs.getLong(12))
                                    .firstName(rs.getString(13))
                                    .lastName(rs.getString(14))
                                    .email(rs.getString(15))
                                    .build());
                        }
                    }
                    if (!developerList.isEmpty()) {
                        solution.setDevelopers(developerList);
                    }
                    optionalSolution = Optional.of(solution);
                }
            }
        } catch (SQLException e) {
            log.error("OR: getSolution SQLException");
            e.printStackTrace();
        }
        return optionalSolution;
    }

    @Override
    public int deleteSolution(long id) {
        String sql = "DELETE FROM solutions WHERE id = ?";
        int numOfDeleted = 0;
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            numOfDeleted = ps.executeUpdate();

        } catch (SQLException e) {
            log.error("DR: getSolution SQLException");
            e.printStackTrace();
        }
        log.info("DR: delete Solution by id={}, num of deleted={} ", id, numOfDeleted);
        return numOfDeleted;
    }

    @Override
    public boolean setSolutionDeveloper(Long solutionId, Long developerId) {
        String sql = "INSERT INTO solutions_developers(solutions_id, developer_id) VALUES (?, ?) ";
        log.info("SR: add Link solutionId={} developerId={}", solutionId, developerId);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, solutionId);
            ps.setLong(2, developerId);
            ps.executeUpdate();
            return true;
        } catch (SQLException | RuntimeException e) {
            log.error("OR: setSolutionDeveloper SQLException");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setSolutionOwner(Long solutionId, Long ownerId) {
        String sql = "UPDATE solutions SET owner_id = ? WHERE id = ? ";
        log.info("SR: add Link solutionId={} ownerId={}", solutionId, ownerId);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ownerId);
            ps.setLong(2, solutionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException | RuntimeException e) {
            log.error("OR: setSolutionOwner SQLException");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Developer> addDeveloper(Developer newDeveloper) {
        Optional<Developer> ownerOptional;
        String sql = "INSERT INTO developers (last_name, first_name, email) VALUES (?, ?, ?)";

        long resultId = -1;
        log.info("DR: add Developer={} ", newDeveloper);

        loadDbDriver();
        try (
                Connection connection = DriverManager.getConnection(dbUrl, username, password);
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, newDeveloper.getLastName());
            ps.setString(2, newDeveloper.getFirstName());
            ps.setString(3, newDeveloper.getEmail());
            ps.executeUpdate();
            if (ps.getGeneratedKeys().next()) {
                resultId = ps.getGeneratedKeys().getLong(1);
                newDeveloper.setId(resultId);
            }
            ownerOptional = Optional.of(newDeveloper);
        } catch (
                SQLException e) {
            log.error("DR: addDeveloper SQLException");
            e.printStackTrace();
            ownerOptional = Optional.empty();
        }
        log.info("DR: add Developer={}, newId={} ", ownerOptional, resultId);
        return ownerOptional;
    }

    @Override
    public Optional<Developer> getDeveloper(long id, boolean isLazy) {
        Optional<Developer> developerOptional = Optional.empty();
        String sql;
        if (isLazy) {
            sql = "SELECT * " +
                    "FROM developers d " +
                    "WHERE d.id = ?";
        } else {
            sql = "SELECT *" +
                    "FROM developers d " +
                    "LEFT JOIN solutions_developers sd on d.id = sd.developer_id " +
                    "LEFT JOIN solutions s on sd.solutions_id = s.id " +
                    "WHERE d.id = ?";
        }
        log.info("DR: get Developer by id={} ", id);
        loadDbDriver();
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    log.debug(i + ":" + rsmd.getColumnName(i));
                }
                Developer developer = Developer.builder().build();
                List<Solution> solutionList = new ArrayList<>(Collections.emptyList());
                while (rs.next()) {
                    if (developer.getEmail() == null) {
                        developer.setId(rs.getLong(1));
                        developer.setLastName(rs.getString(2));
                        developer.setFirstName(rs.getString(3));
                        developer.setEmail(rs.getString(4));
                    }
                    if (!isLazy && rs.getObject(8) != null) {
                        solutionList.add(Solution.builder()
                                .id(rs.getLong(8))
                                .name(rs.getString(9))
                                .version(rs.getString(10))
                                .build()
                        );
                    }
                    developerOptional = Optional.of(developer);
                }
                if (!isLazy) {
                    developer.setSolutions(solutionList);
                }
            }
        } catch (SQLException e) {
            log.error("DR: getDeveloper SQLException");
            e.printStackTrace();
            developerOptional = Optional.empty();
        }
        return developerOptional;
    }

    @Override
    public int deleteDeveloper(long id) {
        String sql = "DELETE FROM developers WHERE id = ?";

        int numOfDeleted = 0;
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            numOfDeleted = ps.executeUpdate();
        } catch (SQLException e) {
            log.error("DR: getDeveloper SQLException");
            e.printStackTrace();
        }
        log.info("DR: delete Developer by id={}, num of deleted={} ", id, numOfDeleted);
        return numOfDeleted;
    }

    private void loadDbDriver() {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            log.error("Load DB Driver error");
            e.printStackTrace();
        }
    }
}
