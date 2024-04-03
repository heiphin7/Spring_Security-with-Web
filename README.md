# Spring_Security-with-Web

Данный проект - полноценный веб-сайт, разработанный для взаимодействия с блогами, где вы можете сохранять блоги, создавать и оценивать. На данном сайте реализована система аутентификации с JWT токеном и cookie - файлами, базовые CRUD - операции с базой данных, динамические страницы отображающие данные из базы и взаимодействующие между собой frontend и backend. Использовался паттерн проектирования MVC(Model - View - Controller) который является составной частью Spring web.

А также, чтобы пользоваться защищенными эндпойнтами, такими как /blogs/**, где находятся все основные страницы, нужно авторизоваться, иначе если неавторизованный пользователь перейдет к защищенным эндпойнтам /blogs/**, то будет ошибка 401 Unauthorized, это все настроекно с помощью Spring security

# Technology stack

## Backend 

+ Java: Основной язык программирования для разработки
+ Spring boot: Фреймвопрк для создания веб - приложения
+ Spring Security: Фреймворк для обеспечения аутентификации и авторизации в приложении
+ JWT (Json web token): Механизм для создания и проверки токенов в контексте аутентификации
+ Spring Data JPA:  интерфейсы, которые можно определять для получения доступа к данным
+ Hibernate: ORM (объектно - реляционное отображение), фреймворк для работы с базой данных
+ PostreSQL: Реляционная база данных
+ Lombok: Библиотека, предоставляющая аннотации для сокращениия кода
+ Maven: Инструмент для управления зависимостями проекта

## Frontend

- Thymeleaf: Шаблонизатор для создания страниц, взаимодействующих с Spring
- HTML/CSS/JS : Базовый стек для создания страниц
- Bootstrap: Фреймворк для создания страниц на основе шаблонов

# Связи и Табилцы в базе данных

Здесь представлены связи таблиц в базе данных

![alt text](https://github.com/heiphin7/Spring_Security-with-Web/blob/main/database%20references.png)


# Иллюстрации

### Main page

![alt text](https://github.com/heiphin7/Spring_Security-with-Web/blob/main/images/main%20page.png)

### Sign Up Form

![alt text](https://github.com/heiphin7/Spring_Security-with-Web/blob/main/images/sing%20up.png)

### Sing in Form

![alt text](https://github.com/heiphin7/Spring_Security-with-Web/blob/main/images/Sing%20in.png)

### Add post From

![alt text](https://github.com/heiphin7/Spring_Security-with-Web/blob/main/images/add-post.png)



# Функционал проекта

+ Регистрация нового аккаунта: Перед пользованием сайта вам необхожимо зарегистровать нового пользователя. В серверной части настроено много валидаторов, которые обеспечивают целостность и правильность данных, отправляемых на базу. Пример валидаторо: Все строки не должны быть пустыми, Имя пользователя не должно быть занято, Почта должна быть корректной, А также поля не должны содержать пробелы, пароль и его подтверждение должны совпадать
+ Авторизация: В отличие от регистрации, авторизация требует всего 2 данные: Имя пользователя и пароль. Тут также настроены базовые валидаторы и проверка данных пользователя происходит автоматический, через AuthenticationManager и его метода autenticate.
+ Создание блога: Также реализована возможность создания блога, где также присутствуют валидаторы. Самая интересная возможность это возможность добавление изображения для блога, для этого вы должны вставить ссылку на изображение, если же со ссылкой что - то не так, валидатор напомнит вам об этом.
+ Добавление в список избранных: В главной странице будут отабражаться все блоги, и перейдя на страницу определенного блога, вы можете добавить его в избранное, а затем перейдя на страницу Favorites, вы увидите все блоги, сохраненные вами
+ Пагинация: Как указывалось выше, в главной странице отображаются все блоги. Но тогда вопрос: А если блогов очень много, например 20, в этом случае я добавил пагинацию (распределение блогов по страницами)


# Логика работы JWT

[alt text](https://github.com/heiphin7/Spring_Security-with-Web/blob/main/database.png)

## Генерация JWT

Для того, чтобы пользователь мог получить JWT токен, ему нужно успешно войти в аккаунт. Это происходит в LoginController, Где мы сначала проверяем его данные, затем только генерируем ему токен, вот как это выглядит в коде:

```java
          try {
            UserDetails userDetails = userService.loadUserByUsername(loginUserDTO.getUsername());

            // Аутентификация пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword(), userDetails.getAuthorities())
            );

            // Установка аутентифицированного пользователя в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Генерация токена по UserDetails
            String token = jwtTokenUtils.generateToken(userDetails);

            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setPath("/blogs/");
            cookie.setMaxAge(86400); // 24 hours
            cookie.setHttpOnly(true);

            // Добавление cookie в responce(ответ от сервера)
            response.addCookie(cookie);
            return "redirect:/blogs/main";

            // Ошибка BadCreadential
        } catch (BadCredentialsException ex) {
            logger.error("Ошибка аутентификации", ex);
            errors.rejectValue("username", "authenticationError", "Неверное имя пользователя или пароль");
            return "loginpage";
        }
```
Сам метод генерации токена в JwtTokenUtils:

```java
public String generateToken(UserDetails user){
        Map<String, Object> claims = new HashMap<>();

        // Получаем его роли, используя stream api
        List<String> roles = user.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).collect(Collectors.toList());

        // Добавляем его роли в список ролей
        claims.put("roles", roles);

        // Дата подписания и дата истечения jwt токена  
        Date issuedAt = new Date();

        // Здесь, чтобы посчитать дату истечения токена, мы просто берем и добавляем к дате подписания время жизни jwt, указанное в конфиге
        Date expiredDate = new Date(issuedAt.getTime() + lifetime.toMillis());

        // И все это дела собираем и подписываем нашим secret-ом
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
```

Проверка токена. Данный метод используется в JwtRequestFilter, который срабатывает при каждом запросе в защищенную область. Здесь, токен нам достают и передают из cookie.
Здесь мы извлекаем данные из токена и при возникновении любой ошибки, связанный с токеном выдаем false. Например могут быть такие ошибки как: Expired(Истек токен), Invalid Signature(Неправильная подпись),
Invalid Token Format(Неправильный формат). Все эти ошибки могут возникнуть только тогда, когда пользователь попытается изменить свой токен.

```java
public boolean validateToken(String token) {
        try {
            // Разбираем токен и извлекаем данные
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            
            // Проверяем, не истек ли срок действия токена
            Date expiration = claims.getExpiration();
            
            return !expiration.before(new Date());
        } catch (Exception e) {
            // Ошибка при проверке токена
            return false;
        }
    }
```


# Установка и Запуск

Для установки проекта вам необходима последняя версия JDK а также ваша любимая среда разработки.


Перед тем, как пользоваться проектом, вам необходимо его установить:
в командной строке пропишите:

git clone https://github.com/heiphin7/Spring_Security-with-Web.git

Далее, нужно установить зависимости проекта командой mvn install
Затем вам нужно открыть проект в IDE и нажать на кнопку старта.

Если у вас возникли ошибки вам нужно посмотреть на конфигурацию в application.yaml и настроить его под себя, например может возникнуть проблема с портом, так как другой ваш проект уже может занимать указанный порт

После выполнения данных действий, вам нужно перейти на http://localhost:8008/login. На этом все








