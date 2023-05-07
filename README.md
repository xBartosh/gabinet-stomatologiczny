# Gabinet stomatologiczny

### What you need to run this app:
1. **Java 17.0.4 JDK installed** - follow [this tutorial](https://www.youtube.com/watch?v=pqqY9jakmFw)
2. **Download MYSQL Workbench** - follow [this tutorial](https://www.youtube.com/watch?v=GoQq5D_ntiY)
   - Set the `password` for MYSQL Workbench local connection to `admin`
   - You can set your own password, but remember to set property `password` in application.yml to `your password`
   - Create a `schema` in MYSQL Workbench local connection named `gabinet-stomatologiczny`
   - Please `DO NOT` push your changes in `application.yml` file! This is a config file
3. **Preferable IDE is IntelliJ** - [link to download](https://www.jetbrains.com/idea/download/#section=windows)
   - You can easily apply for Ultimate Edition for students [here](https://www.jetbrains.com/shop/eform/students)

### How to run the app:
1. Run the main class `GabinetStomatologicznyApplication.java`
2. Open web browser with url `localhost:8080`
3. To view documentation, go to `localhost:8080/swagger-ui/index.html`
