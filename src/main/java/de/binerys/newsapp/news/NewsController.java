package de.binerys.newsapp.news;

import de.binerys.newsapp.news.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NewsController {
    private NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public String showRecentNews(Model model, @RequestParam(name = "count", defaultValue = "5") int count) {
        List<NewsItem> news = newsService.findNews(count);
        model.addAttribute("news", news);
        return "News";
    }
}
