import java.util.ArrayList;
import java.util.List;

public class UserActionService {
    private List<User> userList; // Danh sách người dùng

    public UserActionService() {
        userList = new ArrayList<>(); // Khởi tạo danh sách người dùng
    }

    // Các phương thức khác...

    public void handleUserAccess(UserData userData) {
        switch (userData.userType().getUserType()) {
            case "Admin" -> grantAdminAccess((Admin) userData.userType());
            case "Moderator" -> grantModeratorAccess((Moderator) userData.userType());
            case "Regular User" -> grantRegularUserAccess((RegularUser) userData.userType());
            default -> throw new IllegalStateException("Unexpected value: " + userData.userType());
        }
    }

    private void grantAdminAccess(Admin admin) {
        System.out.println(admin.getUsername() + " has full access to the system.");
        System.out.println("Admin privileges: Create/Delete Users, Manage System Settings.");
    }

    private void grantModeratorAccess(Moderator moderator) {
        System.out.println(moderator.getUsername() + " can manage content.");
        System.out.println("Moderator privileges: Edit/Delete Content.");
    }

    private void grantRegularUserAccess(RegularUser regularUser) {
        System.out.println(regularUser.getUsername() + " can view content.");
        System.out.println("User privileges: View Content, Comment.");
    }

    public void deleteUser(String username) {
        User userToDelete = findUserByUsername(username);
        if (userToDelete != null) {
            userList.remove(userToDelete); // Xóa người dùng khỏi danh sách
            System.out.println("User " + username + " deleted successfully.");
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private User findUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) { // Giả sử có phương thức getUsername()
                return user;
            }
        }
        return null; // Không tìm thấy người dùng
    }
}
