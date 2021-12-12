package dbconn;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {
    @Test
    void testGetConnection() {
        Connection conn = DBConnection.getConnection();
        assertNotNull(conn);
    }
}
