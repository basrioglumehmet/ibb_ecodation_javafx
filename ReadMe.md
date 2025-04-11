# ! ATTENTION NOT FINISHED YET !
ENDS AT (13)
# Project Objective:
The goal is to develop a desktop application based on JavaFX, which includes functionalities such as VAT calculations, user management, and system settings.

# Actions Taken in the Project:

1. **Authentication & Authorization**
2. **Bcrypt Encryption**
3. **User Management & VAT Management**
4. **Filter Operations**
5. **Export to PDF, TXT, or Excel**
6. **Send mail to the target email via SendGrid API**
7. **Print data to the printer feature**
8. **Chart Graphics**
9. **Database First Approach**
10. **User Notifications stored in the database as a model**
11. **Profile Management**
12. **Animated Notes**
13. **Animated Scene Loader**
14. **Redux-based State Management**
15. **MsSQL RDMS (Relational Database Management System)**
16. **Inversion of Control (IoC)**
17. **MVC Architecture with Layered Design**
18. **User Document (WebView Integration)**
19. **Dark/Light Mode Toggle**
20. **Internationalization (i18n)**
21. **Custom Components inspired by shadcn**
22. **Log4j and Security Logging**
23. **Custom Annotations to Enhance Development**
24. **Modular Database Connection (supports MSSQL or H2)**
25. **MapStruct for Object Mapping**
26. **Custom Validation System similar to FluentValidation**
27. **Optimistic Locking**
28. **User OTP (One-Time Password) Verification**
29. **Native Notifications**
30. **Backups (Currently in Development)**
31. **Responsive Layouts**
32. **Custom Movable Scenes**
33. **Custom GUI Animations**
34. **Spring Framework**

# UI Introduction

## Auth Pages
![img_12.png](img_12.png)
![img_13.png](img_13.png)
![img_11.png](img_11.png)
## Home Page
1. ![img_3.png](img_3.png)

2. In the Home section:

    - The **left side** is for user management operations such as:
        - **Filter by**
        - **Refresh**
        - **Print**
        - **Update**
        - **Delete**
        - **Create**

    - The **right side** is for VAT management operations such as:
        - **Filter by VAT number**
        - **Refresh**
        - **Print**
        - **Export** (as PDF, TXT, or Excel)
        - **Update**
        - **Delete**
        - **Send mail to a target email**
        - **Create**

3. At the bottom, there is a **bar chart** displaying a **graphical analysis**.

## User Notifications (Native and DB)

![img_4.png](img_4.png)

1. Users can view **error**, **information**, and other types of notifications.
2. **Success**: Displays a tick icon to the user.
3. **Info**: Displays an "i" icon.
4. **Error**: Displays an "X" icon.
5. **Real-time native notifications** are sent and also saved to the **database**.

## User Profile

![img_5.png](img_5.png)

Displays the current user's information and provides profile management features.

## User Notes

![img_6.png](img_6.png)

1. Users can set a timer for a specific time.
2. Users can create and remove notes.


## Application Configs

![img_7.png](img_7.png)

1. Users can change the UI language.
2. Users can change the UI theme.
3. Users can disable native notifications.
4. **Important**: Users must save the current configurations.

## User Guide
![img_8.png](img_8.png)

### Mail Sending
![img_2.png](img_2.png)
### Printer Feature
![img.png](img.png)