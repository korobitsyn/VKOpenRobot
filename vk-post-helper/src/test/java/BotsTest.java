import com.alkor.vph.bots.FriendsInviteBot;
import com.alkor.vph.bots.GroupWallPostBot;
import com.alkor.vph.bots.VacancyPostBot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Author: akorobitsyn
 * Date: 29.07.13
 * Time: 18:00
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class BotsTest {

    @Test
    public void testFriendsInviteBot() throws IOException, InterruptedException {
        FriendsInviteBot bot = new FriendsInviteBot("Android", 10, 10);
        Thread thread = new Thread(bot);
        thread.run();
    }

    @Test
    public void testGroupsWallPostBoot() throws IOException, InterruptedException {
//        GroupWallPostBot groupWallPostBot = new GroupWallPostBot("android", 1000);
//        GroupWallPostBot groupWallPostBot = new GroupWallPostBot("путешествия", 1000);
//        GroupWallPostBot groupWallPostBot = new GroupWallPostBot("андроид", 1000);
//        GroupWallPostBot groupWallPostBot = new GroupWallPostBot("метро", 1000);
        GroupWallPostBot groupWallPostBot = new GroupWallPostBot("смартфон", 1000);

        Thread thread = new Thread(groupWallPostBot);
        thread.run();
    }

    @Test
    public void testVacancyBootTest() throws IOException {
        VacancyPostBot vacancyPostBot = new VacancyPostBot();
        Thread thread = new Thread(vacancyPostBot);
        thread.run();
    }


}