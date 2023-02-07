package com.cognizant.Auth.model;

import java.util.Objects;

public class TokenResponse {

    private String jwt;
    private int userId;

    public TokenResponse() {
    }

    public TokenResponse(String jwt) {
        this.jwt = jwt;
    }

    public TokenResponse(String jwt, int userId) {
        this.jwt = jwt;
        this.userId = userId;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenResponse that = (TokenResponse) o;
        return Objects.equals(jwt, that.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwt);
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "jwt='" + jwt + '\'' +
                ", userId=" + userId +
                '}';
    }
}
