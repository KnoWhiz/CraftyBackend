package org.curastone.Crafty.controller;

import java.util.List;
import org.curastone.Crafty.dao.NoteDao;
import org.curastone.Crafty.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteController {
  @Autowired private NoteDao noteDao;

  @GetMapping
  public List<Note> getAllNotes() {
    return noteDao.findAll();
  }

  @PostMapping
  public Note createNote(@RequestBody Note note) {
    return noteDao.save(note);
  }

  @GetMapping("/{id}")
  public Note getNoteById(@PathVariable String id) {
    return noteDao.findById(id).orElse(null);
  }

  @PutMapping("/{id}")
  public Note updateNote(@PathVariable String id, @RequestBody Note noteDetails) {
    Note note = noteDao.findById(id).orElse(null);
    if (note != null) {
      note.setContent(noteDetails.getContent());
      note.setSlideId(noteDetails.getSlideId());
      return noteDao.save(note);
    }
    return null;
  }

  @DeleteMapping("/{id}")
  public void deleteNote(@PathVariable String id) {
    noteDao.deleteById(id);
  }
}
