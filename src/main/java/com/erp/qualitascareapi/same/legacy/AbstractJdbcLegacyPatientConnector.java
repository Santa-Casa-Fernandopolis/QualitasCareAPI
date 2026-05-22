package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSex;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

abstract class AbstractJdbcLegacyPatientConnector implements LegacyPatientConnector {

    private final Environment environment;
    private final TextEncryptor encryptor;

    protected AbstractJdbcLegacyPatientConnector(Environment environment, TextEncryptor encryptor) {
        this.environment = environment;
        this.encryptor = encryptor;
    }

    protected abstract SameSourceSystem sourceSystem();

    protected abstract List<LegacyConnectionConfig> activeConnections(Long tenantId);

    @Override
    public boolean supports(SameSourceSystem sourceSystem) {
        return sourceSystem() == sourceSystem;
    }

    @Override
    public List<LegacyPatientRecord> searchByCpf(Long tenantId, String cpf) {
        return queryAll(tenantId, "query-by-cpf", defaultQueryByCpf(), cpf);
    }

    @Override
    public List<LegacyPatientRecord> searchByMedicalRecordCode(Long tenantId, SameSourceSystem sourceSystem, String code) {
        return queryAll(tenantId, "query-by-medical-record-code", defaultQueryByMedicalRecordCode(), code);
    }

    @Override
    public List<LegacyPatientRecord> searchByNameAndBirthDate(Long tenantId, String name, LocalDate birthDate) {
        return queryAll(tenantId, "query-by-name-birth-date", defaultQueryByNameBirthDate(), name, Date.valueOf(birthDate));
    }

    @Override
    public Optional<LegacyPatientRecord> getByExternalPatientId(Long tenantId, String id) {
        return queryAll(tenantId, "query-by-external-id", defaultQueryByExternalId(), id)
                .stream()
                .findFirst();
    }

    protected String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            return null;
        }
        try {
            return encryptor.decrypt(encryptedPassword);
        } catch (Exception ignored) {
            return encryptedPassword;
        }
    }

    private List<LegacyPatientRecord> queryAll(Long tenantId, String queryKey, String defaultQuery, Object... args) {
        String sql = environment.getProperty(propertyPrefix() + "." + queryKey, defaultQuery);
        return activeConnections(tenantId).stream()
                .flatMap(config -> jdbcTemplate(config).query(sql, this::mapRow, args).stream())
                .toList();
    }

    private JdbcTemplate jdbcTemplate(LegacyConnectionConfig config) {
        return new JdbcTemplate(dataSource(config));
    }

    private DataSource dataSource(LegacyConnectionConfig config) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(config.jdbcUrl());
        dataSource.setUsername(config.username());
        String password = decryptPassword(config.encryptedPassword());
        if (password != null) {
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    private LegacyPatientRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LegacyPatientRecord(
                sourceSystem(),
                getString(rs, "external_patient_id"),
                getString(rs, "medical_record_code"),
                getString(rs, "full_name"),
                getString(rs, "mother_name"),
                getLocalDate(rs, "birth_date"),
                getString(rs, "cpf"),
                getString(rs, "cns"),
                getSex(rs, "sex"),
                getString(rs, "raw_payload_json")
        );
    }

    private String propertyPrefix() {
        return "same.legacy." + sourceSystem().name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private String defaultQueryByCpf() {
        return defaultSelect() + " WHERE cpf = ?";
    }

    private String defaultQueryByMedicalRecordCode() {
        return defaultSelect() + " WHERE medical_record_code = ?";
    }

    private String defaultQueryByNameBirthDate() {
        return defaultSelect() + " WHERE UPPER(full_name) LIKE UPPER(?) AND birth_date = ?";
    }

    private String defaultQueryByExternalId() {
        return defaultSelect() + " WHERE external_patient_id = ?";
    }

    private String defaultSelect() {
        return """
                SELECT
                    external_patient_id,
                    medical_record_code,
                    full_name,
                    mother_name,
                    birth_date,
                    cpf,
                    cns,
                    sex,
                    NULL AS raw_payload_json
                FROM pacientes
                """;
    }

    private String getString(ResultSet rs, String column) throws SQLException {
        return rs.getString(column);
    }

    private LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        Date date = rs.getDate(column);
        return date == null ? null : date.toLocalDate();
    }

    private SameSex getSex(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        if (value == null || value.isBlank()) {
            return SameSex.UNKNOWN;
        }
        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "F", "FEMININO", "FEMALE" -> SameSex.FEMALE;
            case "M", "MASCULINO", "MALE" -> SameSex.MALE;
            default -> SameSex.UNKNOWN;
        };
    }

    protected record LegacyConnectionConfig(String jdbcUrl, String username, String encryptedPassword) {
    }
}
