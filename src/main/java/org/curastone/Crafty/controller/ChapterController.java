package org.curastone.Crafty.controller;

import java.util.List;
import org.curastone.Crafty.dao.ChapterDao;
import org.curastone.Crafty.model.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chapters")
public class ChapterController {
    @Autowired
    private ChapterDao chapterDao;

    @GetMapping
    public List<Chapter> getAllChapters() {
        return chapterDao.findAll();
    }

    @PostMapping
    public Chapter createChapter(@RequestBody Chapter chapter) {
        return chapterDao.save(chapter);
    }

    @GetMapping("/{id}")
    public Chapter getChapterById(@PathVariable String id) {
        return chapterDao.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Chapter updateChapter(@PathVariable String id, @RequestBody Chapter chapterDetails) {
        Chapter chapter = chapterDao.findById(id).orElse(null);
        if (chapter != null) {
            chapter.setTitle(chapterDetails.getTitle());
            chapter.setSlideIds(chapterDetails.getSlideIds());
            return chapterDao.save(chapter);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteChapter(@PathVariable String id) {
        chapterDao.deleteById(id);
    }
}
