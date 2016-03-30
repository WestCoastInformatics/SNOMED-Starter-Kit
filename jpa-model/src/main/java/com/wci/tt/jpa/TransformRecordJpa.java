/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.builtin.LongBridge;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;

/**
 * JPA enabled implementation of {@link TransformRecord}.
 */

@Entity
@Table(name = "records", uniqueConstraints = @UniqueConstraint(columnNames = {
    "inputString", "id"
}) )
@Audited
@Indexed
@XmlRootElement(name = "record")
public class TransformRecordJpa implements TransformRecord {

  /** The id. */
  @TableGenerator(name = "EntityIdGenTransformer", table = "table_generator_transformer", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenTransformer")
  private Long id;

  /** the timestamp. */
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp = new Date();

  /** The last modified. */
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified = null;

  /** The last modified. */
  @Column(nullable = false)
  private String lastModifiedBy;

  /** The input string. */
  @Column(nullable = false, length = 4000)
  private String inputString;

  /** The output string. */
  @OneToMany(targetEntity = ScoredResultJpa.class, cascade = CascadeType.ALL)
  @CollectionTable(name = "record_outputs")
  @IndexedEmbedded
  private List<ScoredResult> outputs;

  /** The normalized input strings. */
  @OneToMany(targetEntity = ScoredResultJpa.class, cascade = CascadeType.ALL)
  @CollectionTable(name = "record_normalized_results")
  @IndexedEmbedded
  private List<ScoredResult> normalizedResults;

  /** The input context. */
  @OneToOne(targetEntity = DataContextJpa.class, cascade = CascadeType.ALL)
  @IndexedEmbedded
  private DataContext inputContext = null;

  /** The output context. */
  @OneToOne(targetEntity = DataContextJpa.class, cascade = CascadeType.ALL)
  @IndexedEmbedded
  private DataContext outputContext = null;

  /** The non-persisted provider output context. */
  @Transient
  private DataContext providerOutputContext = null;

  /** The characteristics. */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = true)
  Map<String, String> characteristics;

  /** The statistics. */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = true)
  private Map<String, Double> statistics;

  /**
   * Instantiates an empty {@link TransformRecordJpa}.
   */
  public TransformRecordJpa() {
    // n/a
  }

  /**
   * Instantiates a {@link TransformRecordJpa} from the specified parameters.
   *
   * @param record the record
   */
  public TransformRecordJpa(TransformRecord record) {
    id = record.getId();
    timestamp = record.getTimestamp();
    lastModifiedBy = record.getLastModifiedBy();
    lastModified = record.getLastModified();
    inputString = record.getInputString();
    normalizedResults = new ArrayList<>(record.getNormalizedResults());
    outputs = new ArrayList<>(record.getOutputs());
    inputContext = record.getInputContext();
    outputContext = record.getOutputContext();
    providerOutputContext = record.getProviderOutputContext();
    characteristics = new HashMap<>(record.getCharacteristics());
    statistics = new HashMap<>(record.getStatistics());
  }

  /* see superclass */
  @FieldBridge(impl = LongBridge.class)
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public Long getId() {
    return id;
  }

  /* see superclass */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /* see superclass */
  @Override
  public Date getTimestamp() {
    return timestamp;
  }

  /* see superclass */
  @Override
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public Date getLastModified() {
    return lastModified;
  }

  /* see superclass */
  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /* see superclass */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /* see superclass */
  @Fields({
      @Field(name = "inputString", index = Index.YES, store = Store.NO, analyze = Analyze.YES, analyzer = @Analyzer(definition = "noStopWord") ),
      @Field(name = "inputStringSort", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  })
  @Override
  public String getInputString() {
    return inputString;
  }

  /* see superclass */
  @Override
  public void setInputString(String inputString) {
    this.inputString = inputString;
  }

  /* see superclass */
  @XmlElement(type = ScoredResultJpa.class)
  @Override
  public List<ScoredResult> getNormalizedResults() {
    if (normalizedResults == null) {
      normalizedResults = new ArrayList<>();
    }
    return normalizedResults;
  }

  /* see superclass */
  @Override
  public void setNormalizedResults(List<ScoredResult> normalizedResults) {
    this.normalizedResults = normalizedResults;
  }

  /* see superclass */
  @XmlElement(type = DataContextJpa.class)
  @Override
  public DataContext getInputContext() {
    return inputContext;
  }

  /* see superclass */
  @Override
  public void setInputContext(DataContext inputContext) {
    this.inputContext = inputContext;
  }

  /* see superclass */
  @XmlElement(type = ScoredResultJpa.class)
  @Override
  public List<ScoredResult> getOutputs() {
    if (outputs == null) {
      outputs = new ArrayList<>();
    }
    return outputs;
  }

  /* see superclass */
  @Override
  public void setOutputs(List<ScoredResult> outputs) {
    this.outputs = outputs;
  }

  /* see superclass */
  @XmlElement(type = DataContextJpa.class)
  @Override
  public DataContext getOutputContext() {
    return outputContext;
  }

  /* see superclass */
  @Override
  public void setOutputContext(DataContext outputContext) {
    this.outputContext = outputContext;
  }

  /* see superclass */
  @XmlElement(type = DataContextJpa.class)
  @Override
  public DataContext getProviderOutputContext() {
    return providerOutputContext;
  }

  /* see superclass */
  @Override
  public void setProviderOutputContext(DataContext outputContext) {
    this.providerOutputContext = outputContext;
  }

  /* see superclass */
  // No indexing of characteristics currently
  @Override
  public Map<String, String> getCharacteristics() {
    if (characteristics == null) {
      characteristics = new HashMap<>();
    }
    return characteristics;
  }

  /* see superclass */
  @Override
  public void setCharacteristics(Map<String, String> characteristics) {
    this.characteristics = characteristics;
  }

  /* see superclass */
  // No indexing of statistics currently
  @Override
  public Map<String, Double> getStatistics() {
    if (statistics == null) {
      statistics = new HashMap<>();
    }
    return statistics;
  }

  /* see superclass */
  @Override
  public void setStatistics(Map<String, Double> statistics) {
    this.statistics = statistics;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "TransformRecordJpa [id=" + id + ", timestamp=" + timestamp
        + ", lastModified=" + lastModified + ", lastModifiedBy="
        + lastModifiedBy + ", inputString=" + inputString + ", outputs="
        + outputs + ", normalizedResults=" + normalizedResults
        + ", inputContext=" + inputContext + ", outputContext=" + outputContext
        + ", providerOutputContext=" + providerOutputContext
        + ", characteristics=" + characteristics + ", statistics=" + statistics
        + "]";
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((characteristics == null) ? 0 : characteristics.hashCode());
    result =
        prime * result + ((inputContext == null) ? 0 : inputContext.hashCode());
    result =
        prime * result + ((inputString == null) ? 0 : inputString.hashCode());
    result = prime * result
        + ((normalizedResults == null) ? 0 : normalizedResults.hashCode());
    result = prime * result
        + ((outputContext == null) ? 0 : outputContext.hashCode());
    result = prime * result + ((providerOutputContext == null) ? 0
        : providerOutputContext.hashCode());
    result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
    result =
        prime * result + ((statistics == null) ? 0 : statistics.hashCode());
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TransformRecordJpa other = (TransformRecordJpa) obj;
    if (characteristics == null) {
      if (other.characteristics != null)
        return false;
    } else if (!characteristics.equals(other.characteristics))
      return false;
    if (inputContext == null) {
      if (other.inputContext != null)
        return false;
    } else if (!inputContext.equals(other.inputContext))
      return false;
    if (inputString == null) {
      if (other.inputString != null)
        return false;
    } else if (!inputString.equals(other.inputString))
      return false;
    if (normalizedResults == null) {
      if (other.normalizedResults != null)
        return false;
    } else if (!normalizedResults.equals(other.normalizedResults))
      return false;
    if (outputContext == null) {
      if (other.outputContext != null)
        return false;
    } else if (!outputContext.equals(other.outputContext))
      return false;
    if (providerOutputContext == null) {
      if (other.providerOutputContext != null)
        return false;
    } else if (!providerOutputContext.equals(other.providerOutputContext))
      return false;
    if (outputs == null) {
      if (other.outputs != null)
        return false;
    } else if (!outputs.equals(other.outputs))
      return false;
    if (statistics == null) {
      if (other.statistics != null)
        return false;
    } else if (!statistics.equals(other.statistics))
      return false;
    return true;
  }

  /* see superclass */
  @XmlTransient
  @Override
  public List<ScoredResult> getValuesToProcess() throws Exception {
    List<ScoredResult> termsToProcess = new ArrayList<>();

    termsToProcess.addAll(getNormalizedResults());

    final ScoredResult result = new ScoredResultJpa();
    result.setValue(inputString);
    result.setScore(1);
    termsToProcess.add(result);

    return termsToProcess;
  }

}
