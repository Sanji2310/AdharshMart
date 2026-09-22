package com.adharsh.adharshmart.dto;

/** One selectable color swatch on a product's detail page. */
public class ColorOptionDTO {
    private String name;
    private String hex;

    public ColorOptionDTO() {
    }

    public ColorOptionDTO(String name, String hex) {
        this.name = name;
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }
}
