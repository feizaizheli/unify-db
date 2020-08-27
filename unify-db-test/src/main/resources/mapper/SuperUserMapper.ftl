<@sql id="queryUserListByGroup">
    {
    "query": {
    "match_all": {}
    },
    "aggs":{
    "${groupAge}":{
    "terms":{
    "field":"age"
    }
    }
    }
    }
</@sql>
<@sql id="queryUserListByGroup1">
    {
    "query": {
    "match_all": {}
    },
    "aggs":{
    "${groupAge}":{
    "terms":{
    "field":"age"
    }
    }
    }
    }
</@sql>


<@sql id="queryUserVoListByStretegyBean">
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