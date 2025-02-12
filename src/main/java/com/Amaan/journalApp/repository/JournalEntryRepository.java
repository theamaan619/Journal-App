package com.Amaan.journalApp.repository;

import com.Amaan.journalApp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;




public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {

}
