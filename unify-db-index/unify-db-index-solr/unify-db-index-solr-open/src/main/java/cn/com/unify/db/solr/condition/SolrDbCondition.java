package cn.com.unify.db.solr.condition;



import cn.com.nwdc.db.condition.DbCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * @author: fanxiaoning
 * @since v1.0.1
 */
@Data
@AllArgsConstructor
public class SolrDbCondition extends DbCondition<SolrQuery> {
}
