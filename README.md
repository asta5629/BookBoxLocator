Book Box Locator
Creators: Bryan Doherty, Jacob Astar


1. What does our program do?
	- The Book Box Finder is smartphone application that enables
	a user various ways to find "book boxes", also known as "Little
	Libraries" where people share and borrow books.  


2. Specific Features:
    1) Locate Boxes by Zipcode
    2) Locate Boxes by City,State (Must use 2 digit abreviation)
    3) Locate the 5 nearest boxes to user.
    4) Locate all boxes within the confined of the visible map.
    5) Transfer the address of the box to the external Google Maps application.


3. Directions for running the application:
    - For back end: 
        1) Clone the BookBoxLocator_service repo from git: https://github.com/asta5629/BookBoxLocator_Service.git.
        2) The virtual environment has been included in git, navigate to {your local repo}/book_box_finder_server
        3) Run the server using the python in the virtual environment by using the command "venv/bin/python manage.py runserver" inside of the outer book_box_finder_server folder

	- For front end: 
        1) Clone the BookBoxLocator repo from git: https://github.com/asta5629/BookBoxLocator.git.
        2) Open the project in Android Studio.
        3) Using the Android Virtual Device Manager in Android Studio, create a virtual device with Android API 29 or higher. 
        4) Using the button in the top bar of Android Studio, build the project and deploy it to the virtual device. 
        5) The application will install on the virtual device and you can now open the application on the device. 
