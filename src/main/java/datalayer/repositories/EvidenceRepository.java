package datalayer.repositories;

import buisness.models.Incident;
import buisness.models.Evidence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvidenceRepository {
    private final String url = "jdbc:mysql://localhost:3306/sda_proj";
    private final String user = "root";
    private final String password = "root";

    public void save(Evidence evidence) {
        String sql = "INSERT INTO evidence (description, file_path, incident_id) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, evidence.getDescription());
            pstmt.setString(2, evidence.getFilePath());
            pstmt.setInt(3, evidence.getIncident().getIncidentId()); // Assuming Incident is associated

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        evidence.setEvidenceId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving evidence: " + e.getMessage());
        }
    }

    public Evidence findById(int evidenceId) {
        String sql = "SELECT * FROM evidence WHERE evidence_id = ?";
        Evidence evidence = null;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, evidenceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    evidence = new Evidence();
                    evidence.setEvidenceId(rs.getInt("evidence_id"));
                    evidence.setDescription(rs.getString("description"));
                    evidence.setFilePath(rs.getString("file_path"));

                    // Retrieve associated Incident (if needed)
                    //int incidentId = rs.getInt("incident_id");
                    // You might need to fetch the Incident from the database using incidentId
                    // For example:
                    //Incident incident = new IncidentRepository().findById(incidentId);
                    //evidence.setIncident(incident);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving evidence by ID: " + e.getMessage());
        }

        return evidence;
    }

    public List<Evidence> findAll() {
        String sql = "SELECT * FROM evidence";
        List<Evidence> evidences = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Evidence evidence = new Evidence();
                evidence.setEvidenceId(rs.getInt("evidence_id"));
                evidence.setDescription(rs.getString("description"));
                evidence.setFilePath(rs.getString("file_path"));

                // Retrieve associated Incident (if needed)
                // ... (similar to findById method)

                evidences.add(evidence);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all evidences: " + e.getMessage());
        }

        return evidences;
    }

    public void update(Evidence evidence) {
        String sql = "UPDATE evidence SET description = ?, file_path = ?, incident_id = ? " +
                "WHERE evidence_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidence.getDescription());
            pstmt.setString(2, evidence.getFilePath());
            pstmt.setInt(3, evidence.getIncident().getIncidentId()); // Assuming you have the Incident object
            pstmt.setInt(4, evidence.getEvidenceId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating evidence: " + e.getMessage());
        }
    }

    public void delete(int evidenceId) {
        String sql = "DELETE FROM evidence WHERE evidence_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, evidenceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting evidence: " + e.getMessage());
        }
    }
}
