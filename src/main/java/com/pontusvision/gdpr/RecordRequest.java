package com.pontusvision.gdpr;

public class RecordRequest {
//    {
//        searchStr: self.searchstr,
//                from: from,
//            to: to,
//            sortBy: self.sortcol,
//            sortDir: ((self.sortdir > 0) ? "+asc" : "+desc")
//    }



    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getSortCol() {
        return sortCol;
    }

    public void setSortCol(String sortCol) {
        this.sortCol = sortCol;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public PVGridSearch getSearch() {
        return search;
    }

    public void setSearch(PVGridSearch search) {
        this.search = search;
    }

    PVGridSearch search;
    Long from;
    Long to;
    String sortCol;
    String sortDir;

}
