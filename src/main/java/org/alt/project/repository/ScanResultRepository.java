package org.alt.project.repository;

import org.alt.project.model.ScanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanResultRepository extends JpaRepository<ScanTask, Integer> { }
