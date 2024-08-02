# BookingSystem

## Technologies Used
- Backend: Java Spring Boot, Spring Data JPA
- Frontend: HTML, CSS, Thymeleaf
- Database: MySQL
- Build Tool: Maven

## Usage
### User
1. Register and Login:
   - Users can register and log in to the system.
   - After logging in, users can make bookings and view their booking history.
3. Making a Booking:
   - Navigate to the booking page.
   - Fill in the required details and submit the form.
5. Viewing Booking History:
   - Navigate to the booking history page to view past bookings.

### Merchant
1. Managing Bookings:
   - View and manage all bookings through the interface.
3. Creating and Managing Forms:
   - Create dynamic forms to collect booking details.
   - Add and manage questions and answers for each form.
   - Visualize form structure.
5. Calendar View:
   - View all bookings in a calendar format.
7. Editing User Booking Main Page:
   - Customize the banner, title, and description on the user booking main page.

### Database Configuration
Update the application.properties file with your MySQL configuration:
```
spring.datasource.url=jdbc:mysql://localhost:3306/bookingsystem
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

```

### Access the application
Open browser and navigate to http://localhost:8081/forms.
