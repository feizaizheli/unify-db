<#--人员首次查询-->
<@sql id="queryUserList1" type="select">
  {
    "query": {
        "bool" : {
            "filter":[
                {
                    "term": {
                        "name": "${name}"
                    }
                }
            ]
        }
    }
}
</@sql>
<@sql id="queryUserVoList" type="select">
{
    "query": {
        "bool" : {
            "filter":[
                {
                    "term": {
                        "name": "${name}"
                    }
                }
            ]
        }
    }
}
</@sql>
<@sql id="queryUserVoListByStretegyBean" type="select">
{
    "query": {
        "bool" : {
            "filter":[
                {
                    "term": {
                        "name": "${name}"
                    }
                }
            ]
        }
    }
}
</@sql>

<@sql id="queryUserListByGroup" type="select">
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "group_by_name": {
      "terms": {
        "field": "name",
        "size": 10000,
        "order": {
          "age": "desc"
        }
      },
      "aggs": {
        "age_sum": {
          "sum": {
            "field": "age"
          }
        }
      }
    }
  }
</@sql>

