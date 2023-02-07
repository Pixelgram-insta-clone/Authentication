package com.cognizant.Auth.model;


import java.util.Objects;

public class ResponseDto {

    private int id;
    private String username;

    public ResponseDto() {
    }

    public ResponseDto(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public ResponseDto(String username, String token) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseDto that = (ResponseDto) o;
        return id == that.id && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
