package org.curastone.Crafty.dao;

import org.curastone.Crafty.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteDao extends MongoRepository<Note, String> {}
