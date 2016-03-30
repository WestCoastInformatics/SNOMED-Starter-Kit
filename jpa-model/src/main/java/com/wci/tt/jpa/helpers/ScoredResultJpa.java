/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.builtin.FloatBridge;
import org.hibernate.search.bridge.builtin.LongBridge;

import com.wci.tt.helpers.ScoredResult;

/**
 * JPA enabled scored implementation of {@link ScoredResult}.
 */
@Entity
@Table(name = "scored_results")
// @Audited - no changing here
@Indexed
@XmlRootElement(name = "scoredResult")
public class ScoredResultJpa implements ScoredResult, Comparable<ScoredResult> {

  /** The id. */
  @TableGenerator(name = "EntityIdGenTransformer", table = "table_generator_transformer", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenTransformer")
  private Long id;

  /** The score. */
  @Column(nullable = false)
  private float score = 0;

  /** The value. */
  @Column(nullable = false)
  private String value;

  /** The obsolete. */
  @Column(nullable = false)
  private boolean obsolete = false;

  /**
   * Instantiates an empty {@link ScoredResultJpa}.
   */
  public ScoredResultJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ScoredResultJpa} from the specified parameters.
   *
   * @param result the scored result
   */
  public ScoredResultJpa(ScoredResult result) {
    this.id = result.getId();
    this.obsolete = result.isObsolete();
    this.value = result.getValue();
    this.score = result.getScore();
  }

  /**
   * Instantiates a {@link ScoredResultJpa} from the specified parameters.
   *
   * @param value the value
   * @param score the score
   */
  public ScoredResultJpa(String value, float score) {
    this.value = value;
    this.score = score;
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
  @FieldBridge(impl = FloatBridge.class)
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public float getScore() {
    return score;
  }

  /* see superclass */
  @Override
  public void setScore(float score) throws Exception {
    this.score = score;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public String getValue() {
    return value;
  }

  /* see superclass */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  /* see superclass */
  @Override
  public boolean isObsolete() {
    return obsolete;
  }

  /* see superclass */
  @Override
  public void setObsolete(boolean obsolete) {
    this.obsolete = obsolete;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (obsolete ? 1231 : 1237);
    result = prime * result + Float.floatToIntBits(score);
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    ScoredResultJpa other = (ScoredResultJpa) obj;
    if (obsolete != other.obsolete)
      return false;
    if (score != other.score)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredResultJpa [id=" + id + ", score=" + score + ", value=" + value
        + ", obsolete=" + obsolete + "]";
  }

  /* see superclass */
  @Override
  public int compareTo(ScoredResult o) {
    Float score1 = new Float(this.getScore());
    Float score2 = new Float(o.getScore());
    return score2.compareTo(score1);
  }
}
