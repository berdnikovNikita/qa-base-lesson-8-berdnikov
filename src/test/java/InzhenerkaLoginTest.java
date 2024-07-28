import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Selenide.*;

public class InzhenerkaLoginTest {
    @Test
    public void loginTest() {
        Configuration.browser= "firefox";
        Configuration.startMaximized = true;
        Configuration.headless = true;
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
}