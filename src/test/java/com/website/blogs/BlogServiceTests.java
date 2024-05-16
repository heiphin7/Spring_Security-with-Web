package com.website.blogs;

import com.website.blogs.entity.Blog;
import com.website.blogs.entity.User;
import com.website.blogs.repository.BlogRepository;
import com.website.blogs.services.BlogService;
import com.website.blogs.validations.URLchecker;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BlogServiceTests {

    @Mock
    private static BlogRepository blogRepository;
    @Mock
    private static URLchecker urLchecker;
    @InjectMocks
    private static BlogService blogService;


    // init mocks & service before all tests
    @BeforeAll
    public static void init() {
        blogRepository = Mockito.mock(BlogRepository.class);
        urLchecker = Mockito.mock(URLchecker.class);

        blogService = new BlogService(
                blogRepository, urLchecker
        );

        // Имитируем работу urlChecker, типа чтобы он давал ответ, что ссылка правильная
        Mockito.when(urLchecker.isValidImageLink("https://pbs.twimg.com/media/FTlYuJLacAAf5o7.jpg")).thenReturn(true);
    }

    @Test
    public void succes_save_blog() {
        // arrange

        // author for blog
        User author = new User();
        author.setUsername("some user");
        author.setEmail("some-email@gmail.com");
        author.setPassword("Password");

        // correct blog
        Blog blogToSave = new Blog();
        blogToSave.setTitle("some title");
        blogToSave.setAnons("some anons");
        blogToSave.setAuthor(author);
        blogToSave.setFulltext("some full text");

        // Вводим корректное изображение, так как она проверяется в BlogService
        blogToSave.setImage("https://pbs.twimg.com/media/FTlYuJLacAAf5o7.jpg");

        // act
        // message это как-бы response от BlogService
        String message = blogService.saveBlog(blogToSave);

        // assert
        Assert.assertEquals("Блог успешно сохранен!", message);
    }

    @Test
    public void blog_with_empty_author() {
        // arrange
        // blog with empty author
        Blog blogToSave = new Blog();
        blogToSave.setTitle("some title");
        blogToSave.setAnons("some anons");
        blogToSave.setAuthor(null); // empty author
        blogToSave.setFulltext("some full text");
        // Вводим корректное изображение, так как она проверяется в BlogService
        blogToSave.setImage("https://pbs.twimg.com/media/FTlYuJLacAAf5o7.jpg");

        // act
        String messge = blogService.saveBlog(blogToSave);

        // assert
        Assert.assertEquals("Нету автора", messge);
    }

    @Test
    public void blog_with_incorrect_imageLink() {
        // arrange
        // author for blog
        User author = new User();
        author.setUsername("some user");
        author.setEmail("some-email@gmail.com");
        author.setPassword("Password");

        // correct blog
        Blog blogToSave = new Blog();
        blogToSave.setTitle("some title");
        blogToSave.setAnons("some anons");
        blogToSave.setAuthor(author);
        blogToSave.setFulltext("some full text");

        // Вводим корректное изображение, так как она проверяется в BlogService
        blogToSave.setImage("alskdjf;askdjf;alksjdf;aksjdf"); // incorrect imageLink

        // act
        String message = blogService.saveBlog(blogToSave);

        // assert
        Assert.assertEquals("Введите корректную ссылку!", message);
    }

    @Test
    public void save_empty_blog() {
        //act
        String messsage = blogService.saveBlog(new Blog());

        System.out.println(new Blog());

        // assert
        Assert.assertEquals("Заполните все поля", messsage);
    }

}
