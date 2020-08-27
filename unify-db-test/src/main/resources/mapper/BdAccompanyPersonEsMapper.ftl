<#setting number_format="#">
<@select>
createMapping:
{
     "settings": {
         "number_of_shards": 1,
         "number_of_replicas": 1
     },
     "mappings": {
         "doc": {
             "dynamic": "false",
             "properties": {
                 "id": {
                     "type": "long"
                 },
                "target_p_id" : {
                     "type": "keyword"
                },
                 "target_face_id": {
                    "type" : "keyword"
                 },
                 "target_collect_time": {
                     "type": "date",
                     "format": "yyyy-MM-dd HH:mm:ss"
                 },
                "accompany_p_id" : {
                    "type" : "keyword"
                },
                 "accompany_face_id": {
                    "type" : "keyword"
                 },
                 "accompany_collect_time": {
                     "type": "date",
                     "format": "yyyy-MM-dd HH:mm:ss"
                 },
                 "ape_id": {
                    "type" : "keyword"
                 },
                 "interval_time": {
                     "type": "keyword"
                 },
                 "create_time": {
                     "type": "date",
                     "format": "yyyy-MM-dd HH:mm:ss"
                 },
                 "storage_date": {
                     "type": "date",
                     "format": "yyyy-MM-dd"
                 }
             }
         }
     }
}
</@select>
<@select id="selectByStorageDate">
selectByStorageDate:
{
    "query": {
        "bool" : {
            "filter":[
                {
                    "term": {
                        "storage_date": "${storageDate}"
                    }
                }
            ]
        }
    }
}
</@select>
<@select>
queryPageInfo:
{
    "query": {
        "bool" : {
            "filter":[
                {
                    "term": {
                        "storage_date": "${storageDate}"
                    }
                }
            ]
        }
    },
    "size":10
}
</@select>
