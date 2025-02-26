package com.Amaan.journalApp.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private ObjectId id;
    // We have to add spring.data.mongodb.auto-index-creation property and make it True
    @Indexed(unique = true)
    @NonNull
    private String userName;
    private String email;
    private String sentimentAnalysis;
    @NonNull
    private String password;
    @DBRef // because of this annotation only reference is stored in our case that is objectId
    private List<JournalEntry> journalEntries = new ArrayList<>();
    private List<String> roles;
}
