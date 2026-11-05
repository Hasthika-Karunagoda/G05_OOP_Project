package com.RockyToys.ToyManagement.service;

import com.RockyToys.ToyManagement.dsa.SelectionSort;
import com.RockyToys.ToyManagement.dto.ToyDTO;
import com.RockyToys.ToyManagement.filehandling.CategoryFileHandler;
import com.RockyToys.ToyManagement.filehandling.ToyFileHandler;
import com.RockyToys.ToyManagement.model.Toy;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ToyService {
    private final ToyFileHandler toyFileHandler;
    private final CategoryFileHandler categoryFileHandler;
    private final List<Toy> toys = new ArrayList<>();

    public ToyService(ToyFileHandler toyFileHandler, CategoryFileHandler categoryFileHandler) {
        this.toyFileHandler = toyFileHandler;
        this.categoryFileHandler = categoryFileHandler;
    }

    @PostConstruct
    public void load() {
        toys.clear();
        toys.addAll(toyFileHandler.readAll());
    }

    public synchronized Toy addToy(ToyDTO dto) {
        Toy toy = toToy(dto);
        if (findIndex(toy.getToyId()) >= 0) throw new IllegalArgumentException("Toy ID already exists: " + toy.getToyId());
        validateCategory(toy.getCategoryId());
        toys.add(toy);
        persist();
        return toy;
    }

    public synchronized Toy updateToy(String toyId, ToyDTO dto) {
        int index = findIndex(toyId);
        if (index < 0) throw new IllegalArgumentException("Toy not found: " + toyId);
        Toy old = toys.get(index);
        Toy updated = new Toy(old.getToyId(), dto.getName(), dto.getDescription(), dto.getCategoryId(), dto.getPrice(), dto.getAgeGroup());
        validateCategory(updated.getCategoryId());
        toys.set(index, updated);
        persist();
        return updated;
    }

    public synchronized void deleteToy(String toyId) {
        int index = findIndex(toyId);
        if (index < 0) throw new IllegalArgumentException("Toy not found: " + toyId);
        toys.remove(index);
        persist();
    }

    public synchronized Toy readToy(String toyId) {
        int index = findIndex(toyId);
        if (index < 0) throw new IllegalArgumentException("Toy not found: " + toyId);
        return toys.get(index);
    }

    public synchronized List<Toy> getAllToys() { return new ArrayList<>(toys); }

    public synchronized List<Toy> searchToy(String query) {
        if (query == null || query.isBlank()) return getAllToys();
        String q = query.trim().toLowerCase(Locale.ROOT);
        return toys.stream().filter(t -> t.getToyId().toLowerCase(Locale.ROOT).contains(q)
            || t.getName().toLowerCase(Locale.ROOT).contains(q)).toList();
    }

    public synchronized List<Toy> getToysByCategory(String categoryId) {
        return toys.stream().filter(t -> t.getCategoryId().equalsIgnoreCase(categoryId)).toList();
    }

    public synchronized List<Toy> sortToysByAgeGroup() { return SelectionSort.sortByAgeGroup(toys); }

    private Toy toToy(ToyDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Toy details are required.");
        return new Toy(dto.getToyId(), dto.getName(), dto.getDescription(), dto.getCategoryId(), dto.getPrice(), dto.getAgeGroup());
    }

    private void validateCategory(String categoryId) {
        if (!categoryFileHandler.categoryExists(categoryId)) {
            throw new IllegalArgumentException("Category does not exist: " + categoryId +
                ". Add the category to data/categories.txt using the category segment.");
        }
    }

    private int findIndex(String toyId) {
        if (toyId == null || toyId.isBlank()) return -1;
        for (int i = 0; i < toys.size(); i++) if (toys.get(i).getToyId().equalsIgnoreCase(toyId.trim())) return i;
        return -1;
    }

    private void persist() { toyFileHandler.writeAll(toys); }
}
