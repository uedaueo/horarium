package ueoware.autotest.examples;

import junit.framework.TestCase;

import com.thoughtworks.selenium.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SeleniumTest extends TestCase {

    DefaultSelenium selenium = null;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*iexplore",
                "http://somewhrere.ueo.co.jp/");
        selenium.start();
    }

    @Test
    public void testSelenium() throws Exception {
        selenium.open("/");
        selenium.type("name=login_id", "t_tueda");
        selenium.type("name=password", "t_tueda");
        selenium.click("css=input[type=image]");
        selenium.waitForPageToLoad("30000");
        selenium.click("//div[@id='head']/form/div/table[2]/tbody/tr/td/a/div");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Teacher'sコース追加");
        selenium.waitForPageToLoad("30000");
        selenium.click("//div[2]/div/div/div/table/tbody/tr/td[2]/a/img");
        assertEquals("セクション情報を追加しますか", selenium.getConfirmation());
        selenium.select("name=course_select", "label=ジュニア４");
        selenium.waitForPageToLoad("30000");
        selenium.select("name=part_select", "label=パート１");
        selenium.waitForPageToLoad("30000");
        selenium.select("name=unit_select", "label=STEP １　英語の単語");
        selenium.waitForPageToLoad("30000");
        selenium.click("name=chk_p");
        selenium.click("name=chk_n");
        selenium.click("name=chk_w");
        selenium.click("css=img");
        assertEquals("選択したコンテンツを追加しますか", selenium.getConfirmation());
        selenium.click("//div[2]/div/div/div/table/tbody/tr/td[4]/a/img");
        assertEquals("セクション情報を削除しますか", selenium.getConfirmation());
        selenium.click("//table[4]/tbody/tr/td[4]/a/img");
        assertEquals("ＢＲＩＸ情報を削除しますか", selenium.getConfirmation());
        selenium.click("//table[4]/tbody/tr/td[4]/a/img");
        assertEquals("ＢＲＩＸ情報を削除しますか", selenium.getConfirmation());
        selenium.click("//table[4]/tbody/tr/td[4]/a/img");
        assertEquals("ＢＲＩＸ情報を削除しますか", selenium.getConfirmation());
        selenium.click("//table[4]/tbody/tr/td[4]/a/img");
        assertEquals("ＢＲＩＸ情報を削除しますか", selenium.getConfirmation());
        selenium.click("//table[4]/tbody/tr/td[4]/a/img");
        assertEquals("ＢＲＩＸ情報を削除しますか", selenium.getConfirmation());
        selenium.type("name=name_C_504", "SeleniumTest");
        selenium.type("name=name_P_1", "パート");
        selenium.type("name=name_U_1", "ユニット");
        selenium.click("css=img");
        assertEquals("このコース情報を保存しますか", selenium.getConfirmation());
        selenium.click("css=div.div_left > a > img");
        selenium.waitForPageToLoad("30000");
        selenium.click("//tr[7]/td[5]/a/img");
        assertEquals("コース情報を確定してもよろしいですか？", selenium.getConfirmation());
        selenium.click("//tr[8]/td[4]/a/img");
        assertEquals("コースを削除してもよろしいですか？", selenium.getConfirmation());
        assertEquals("削除しました。", selenium.getAlert());
        selenium.click("//tr[7]/td[4]/a/img");
        assertEquals("コース情報を削除してもよろしいですか？", selenium.getConfirmation());
        selenium.click("link=トップ");
        selenium.waitForPageToLoad("30000");
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
