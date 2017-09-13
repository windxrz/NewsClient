package com.java.group6.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsBriefList implements Serializable
{
    public List<NewsBrief> list = new ArrayList<>();
    public int pageNo;
    public int pageSize;
    public int totalPages;
    public int totalRecords;
}
