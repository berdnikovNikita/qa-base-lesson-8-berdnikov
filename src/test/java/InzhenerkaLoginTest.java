import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class InzhenerkaLoginTest {
    @BeforeAll
    public static void setUp() {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get("src/test/resources/selenide.properties")));
            Configuration.browser = properties.getProperty("selenide.browser");
            // Deprecated
            //Configuration.startMaximized = Boolean.parseBoolean(properties.getProperty("selenide.startMaximized"));
            Configuration.browserSize = properties.getProperty("selenide.browserSize", "1920x1080");
            Configuration.headless = Boolean.parseBoolean(properties.getProperty("selenide.headless"));
            Configuration.remote = properties.getProperty("selenide.remote");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Disabled("Overridden by textSelectorsLogin")
    @Test
    public void loginTest() {

        // Открыть страницу входа
        Selenide.open("http://qa-stand-login.inzhenerka.tech/login");

        // Ввод логина
        $("#username").setValue("admin");

        // Ввод пароля
        $(By.name("password")).setValue("admin123");

        // Нажатие кнопки входа
        $("button[type='submit']").click();

        // Выход из системы
        $(".mb-3").click();
    }

    @Test
    public void textSelectorsLogin() {

        // Открыть страницу входа
        Selenide.open("http://qa-stand-login.inzhenerka.tech/login");

        // Проверка существования поля логина, CSS-селектор
        if ($("#username").exists()) {
            // Проверка видимости и непредзаполненности поля логина, XPath-селектор
            $(By.xpath("//input[@name='username']")).shouldBe(visible, Duration.ofSeconds(5));
            $(By.xpath("//input[@name='username']")).shouldBe(empty);
            // Ввод логина, By-селектор
            $(By.name("username")).setValue("admin");
        } else org.junit.jupiter.api.Assertions.fail("Username input is not found on the page");

        // Проверка существования поля пароля, By-селектор
        if ($(By.name("password")).exists()) {
            // Проверка видимости и непредзаполненности поля пароля, атрибутный CSS-селектор
            $("input[type='password']").shouldBe(visible);
            $("input[type='password']").shouldBe(empty);
            // Ввод пароля, XPath-селектор
            $(By.xpath("//input[@type='password']")).setValue("admin123");
        } else org.junit.jupiter.api.Assertions.fail("Password input is not found on the page");

        // Проверка существования кнопки входа, XPath-селектор
        if ($(By.xpath("//button[@class='btn btn-primary w-100 loginClass']")).exists()) {
            // Проверка видимости и текста кнопки, By+CSS-селекторы
            $(By.className("loginClass")).shouldBe(visible);
            $(".loginClass").shouldHave(text("Войти"));

            // Нажатие кнопки входа, текстовый селектор ==
            $(withText("Войт")).click();
        } else org.junit.jupiter.api.Assertions.fail("Sign in button is not found on the page");

        // Проверка существования кнопки выхода, XPath-селектор
        if ($(By.xpath("//a[@class='btn btn-danger w-100']")).exists()) {
            // Проверка видимости и текста кнопки, By+CSS-селекторы
            $(By.className("w-100")).shouldBe(visible, Duration.ofSeconds(5));
            $(".btn.btn-danger.w-100").shouldHave(text("Выйти"));

            // Выход из системы, текстовый селектор ~
            $(byText("Выйти")).click();
        } else org.junit.jupiter.api.Assertions.fail("Sign out button is not found on the page");
    }

    /**
     * Загружает валидный файл на сайт csm-testcenter.org
     * Затем отправляет файл через HTTPS и сверяет объем+хэш файла
     */
    @Test
    public void uploadFileSuccess() {
        //Хардкод данных тестового файла test-image.png
        final String fileSHA256 = "8ACDA41C2C63CB3FAFC9E856DC77F685F6CC7B8D7EB94D589EB120FDE1D8DDDB";
        final int fileByteSize = 515221;
        final String fileName = "test-image.png";

        // Открыть страницу загрузки
        Selenide.open("https://www.csm-testcenter.org/test?do=show&subdo=common&test=file_upload");

        // Найти div-блок для отправки через HTTPS
        SelenideElement httpsBlock = $(By.xpath("//div[@id='item'][h1[normalize-space()='File upload via POST (HTTPS)']]"))
                .should(appear, Duration.ofSeconds(5));

        // Найти поле загрузки файла
        SelenideElement httpsInput = httpsBlock.$("input[type='file']")
                .should(appear);

        // Загрузить в поле файл
        httpsInput.uploadFile(new File("src/test/resources/" + fileName));

        // Найти кнопку отправки файла
        SelenideElement httpsUpload = httpsBlock.$("input[name='https_submit']")
                .should(appear);

        // Проверить видимость и текст кнопки
        httpsUpload.shouldBe(visible);
        httpsUpload.shouldHave(value("Start HTTPS upload"));

        // Отправить файл на сервер
        httpsUpload.click();

        // Найти div-блок с результатами загрузки
        SelenideElement resultBlock = $(By.xpath("//div[h1[normalize-space()='Information about the uploaded data']]"))
                .should(appear, Duration.ofSeconds(5));

        // Проверить статус загрузки
        resultBlock.$(By.xpath(".//tr[td[normalize-space()='Status']]/td[2]"))
                .shouldHave(text("File successfully uploaded"));

        // Сверить название файла
        resultBlock.$(By.xpath(".//tr[td[normalize-space()='Filename']]/td[2]"))
                .shouldHave(text("test-image.png"));

        // Сверить размер файла
        resultBlock.$(By.xpath(".//tr[td[normalize-space()='Filesize']]/td[2]"))
                .shouldHave(text(fileByteSize + " bytes"));

        // Сверить хэш файла
        resultBlock.$(By.xpath(".//tr[td[normalize-space()='Content SHA256']]/td[2]"))
                .shouldHave(text(fileSHA256));
    }
}