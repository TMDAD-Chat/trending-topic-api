package es.unizar.tmdad.service;

import es.unizar.tmdad.adt.BagOfWordEntry;

import java.util.Date;
import java.util.List;

public interface BagOfWordsService {

    void addWordsToBag(List<String> terms, Date timestamp);

    List<BagOfWordEntry> getTopWordsOfToday();

}
