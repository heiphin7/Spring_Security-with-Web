var xhr = new XMLHttpRequest();
console.log("Сработал js файл")
xhr.open("GET", "https://localhost:8008/login", true);
xhr.setRequestHeader("Authorization", "Bearer " + accessToken);
xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
        // Обработка успешного ответа от сервера
        console.log(xhr.responseText);
    }
};
xhr.send();
