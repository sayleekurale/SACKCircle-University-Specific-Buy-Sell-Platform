# SACKCircle-University-Specific-Buy-Sell-Platform


SACKCircle is a full-fledged desktop application developed for university students to **buy, sell, and exchange products** on campus. The platform promotes **reuse, sustainability, and convenience**, featuring separate **Admin and Customer logins**, real-time chat, and a responsive Java Swing UI/UX.

---

## 🚀 Features

### 👥 User & Admin Login
- Role-based login system  
- Secure admin credentials stored in MySQL  
- Customer details and product listings stored in MongoDB

### 🛒 Product Management
- List, search, and filter products  
- Add descriptions, categories, and pricing  
- Add to wishlist

### 💬 Chat System

### 🛡️ Admin Dashboard
- View and manage all users  
- Monitor and manage listed products  

### 🎨 UI/UX Design
- Built with **Java Swing** for interactive UI  
- Clean, intuitive layouts with responsive feedback  
- UX design focused on minimal clicks and ease of use

---

## 🧰 Tech Stack

| Component       | Technology     |
|----------------|----------------|
| Frontend UI     | Java Swing/AWT |
| Backend Logic   | Java           |
| Admin DB        | MySQL          |
| Customer DB     | MongoDB        |
| Chat System     | Custom Java    |

---

## 🗃️ Project Structure

```
SACKCircle/src/main/java
├── ui/              # Java Swing UI components
├── services/        # Authentication, chat, and product services
├── models/          # Data models
├── database/        # MongoDB & MySQL connection handlers
├── Main.java        # Entry point
└── README.md
```

---

## 🔌 Setup Instructions

### ✅ Requirements
- Java 8+
- MongoDB installed and running
- MySQL installed and running (Port: `3306`)
- MySQL admin credentials table setup:
```sql
CREATE DATABASE sackcircle;

USE sackcircle;

CREATE TABLE admin (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);
```

### ⚙️ Configuration
Update your database connection details in:
- `MySQLConnection.java` (for admin login)
- `MongoDBConnection.java` (for user and product data)

---

## 📸 Screenshots

![image](https://github.com/user-attachments/assets/a3671de2-5d37-48d5-962d-af4eb086a8a5)
![image](https://github.com/user-attachments/assets/f94e6236-56a8-4bcd-9f37-98543209b78f)
![image](https://github.com/user-attachments/assets/b35eefdf-c6ce-46f9-8e46-a57e00efe264)
![image](https://github.com/user-attachments/assets/fb8eb8d3-8083-43d6-8d63-1d937c4e5e8a)
![image](https://github.com/user-attachments/assets/ae962f29-919a-44de-8082-ccc24de8f87c)
![image](https://github.com/user-attachments/assets/ebbc70aa-d15b-446f-a6d6-d6e0ec798bbc)
![image](https://github.com/user-attachments/assets/cee628be-7e0f-480a-8466-4e9bee26bdc7)
![image](https://github.com/user-attachments/assets/9cc583ad-7391-4bca-984f-b80d355f25ed)
![image](https://github.com/user-attachments/assets/eba93b63-621e-4244-b61b-a935c8fc3b18)



---

## 💡 Future Enhancements
- Push notifications  
- AI integration
- Real Time Chat 
- Mobile version using JavaFX or Android

---

## 🤝 Contributors
- Saylee Kurale (Backend, Frontend, DB Integration)
- 

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
```

---

Let me know if you’d like to include badges, GIFs, GitHub link, or a deployment section!
