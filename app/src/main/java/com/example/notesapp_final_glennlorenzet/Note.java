package com.example.notesapp_final_glennlorenzet;

public class Note {
    private String id;
    private String title;
    private String content;

    public Note(String id, String title, String content)
    {
        setId(id);
        setTitle(title);
        setContent(content);
    }

    String getId()
    {
        return this.id;
    }

    void setId(String id)
    {
        this.id = id;
    }

    String getTitle()
    {
        return this.title;
    }

    void setTitle(String title)
    {
        this.title = title;
    }

    String getContent()
    {
        return this.content;
    }

    void setContent(String content)
    {
        this.content = content;
    }
}
