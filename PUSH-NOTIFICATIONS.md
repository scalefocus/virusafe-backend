# Push notifications endpoints description

The main role of the endpoints starting with `/admin` is to provide functionality to send push notifications to concrete users by defining search criteria or userGuids.

Overview:
- [Endpoints](#endpoints)
  - [POST /admin/pushNotification/query](#query-endpoint)
  - [POST /admin/pushNotification/user](#user-endpoint)
- [Security](#security)

1. POST endpoint - `/admin/pushNotification/query`
    - input: RequestBody - PushNotificationRequestDTO

    Sample PushNotificationRequestDTO
    ```
   {
       "questionnaireQuery": {
           "timeSlot": {
               "gte": "now-1d",
               "lt": "now"
            },
            "answerQuery": {
                "and": [
                  {
                    "eq": {
                      "questionId": 1,
                      "answer": "True"
                    }
                  },
                  {
                    "or": [
                      {
                        "eq": {
                          "questionId": 2,
                          "answer": "True"
                        }
                      },
                      {
                        "eq": {
                          "questionId": 3,
                          "answer": "True"
                        }
                      },
                      {
                        "eq": {
                          "questionId": 5,
                          "answer": "True"
                        }
                      },
                      {
                        "minMatch": 2
                      }
                    ]
                  }
                ]
              },
   		 "polygonPoints": [
   			{
   			  "lat": 30,
   			  "lon": 70
   			},
   			{
   			  "lat": 30,
   			  "lon": 80
   			}
   		  ]
       },
       "title": "ViruSafe-Dev-Push",
       "message": "Push Notification - the answer of the first question should be 'True' and there should be at least 2 true answers in questions 2, 3 and 5 in the last day for concrete locations"
   }
   ```
      Along with the title and the message, there is a questionnaireQuery that describes the fields that will be searched on. 
      For example, in this sample json there is timeSlot, answerQuery and polygonPoints as a search criteria. The query may include one or more of these fields. 
      - timeSlot could be described by some of these fields: String gt(greater than), String lt(less than), String gte(greater than or equivalent to), String lte(less than or equivalent to).
      - answerQuery may include an operation - `and`/`or` with multiple subOperations. Along with describing the different criteria arguments, there is additional minMatch field which points the number of correct conditions that should be met.
      - polygonPoints includes a list of `lat`(latitude) and `lon`(longitude).
      
      Optional parameters:
      - reverseQueryResults boolean flag that add possibility to invert results from `questionnaireQuery`
       
      Having these criteria set, there is a Elasticsearch query that is build and that returns a set of userGuids. Based on these userGuids there is additional search in the SQL database, where the push tokens are stored against the userGuids. These push tokens are used by Firebase when sending notifications.
1. POST endpoint - `/admin/pushNotification/user`
    - input: RequstBody - CustomPushNotificationDTO
    
    Sample CustomPushNotificationDTO
    
    ```
    {
      "userGuids": [
        "f24wgf422d24",
        "tj81sad3sv44h6",
        "a8i55hm2k4g6"
      ],
      "title": "Important notification",
      "message": "Please send your symptoms today"
    }    
    ```
    This endpoint receives a set of concrete userGuids, a title and a message of the notification.

 ### Security
    There is basic authentication for these endpoints. Username and password are required.