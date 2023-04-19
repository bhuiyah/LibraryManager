import java.io.Serializable;

public class Admin implements Serializable {

        private String username;


        private String password;


        private String id;

        public Admin(String username, String password, String id) {
            this.username = username;
            this.password = password;
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getID() {
            return id;
        }

        public void setID(String id) {
            this.id = id;
        }

}
