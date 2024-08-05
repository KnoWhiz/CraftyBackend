package org.curastone.Crafty.controller;

import org.curastone.Crafty.model.Slide;
import org.curastone.Crafty.dao.SlideDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slides")
public class SlideController {
  @Autowired private SlideDao slideDao;

  @GetMapping
  public List<Slide> getAllSlides() {
    return slideDao.findAll();
  }

  @PostMapping
  public Slide createSlide(@RequestBody Slide slide) {
    return slideDao.save(slide);
  }

  @GetMapping("/{id}")
  public Slide getSlideById(@PathVariable String id) {
    return slideDao.findById(id).orElse(null);
  }

  @PutMapping("/{id}")
  public Slide updateSlide(@PathVariable String id, @RequestBody Slide slideDetails) {
    Slide slide = slideDao.findById(id).orElse(null);
    if (slide != null) {
      slide.setContent(slideDetails.getContent());
      slide.setVideoUrl(slideDetails.getVideoUrl());
      return slideDao.save(slide);
    }
    return null;
  }

  @DeleteMapping("/{id}")
  public void deleteSlide(@PathVariable String id) {
    slideDao.deleteById(id);
  }
}
