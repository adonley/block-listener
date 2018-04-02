package io.block16.ethlistener.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EthereumWords {

    @Getter
    private List<String> words;

    public EthereumWords(List<String> words) {
        this.words = words;
        if(this.words == null) {
            this.words = new ArrayList<>();
        }
    }

    public int size() {
        return words.size();
    }

    public String get(int index) {
        return this.words.get(index);
    }

    public String wordToAddress(int index) {
        if(index > words.size())
            throw new IndexOutOfBoundsException();
        String word = this.words.get(index);
        return word.substring(word.length() - 40, word.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EthereumWords that = (EthereumWords) o;
        return words.equals(that.words);
    }

    @Override
    public int hashCode() {
        return words.hashCode();
    }

    @Override
    public String toString() {
        return "EthereumWords{" +
                "words=" + words.stream().map(String::toString).collect(Collectors.joining(", ")) +
                '}';
    }

    public boolean containsWord(String word) {
        return this.words.stream().anyMatch(w -> w.equals(word));
    }
}
