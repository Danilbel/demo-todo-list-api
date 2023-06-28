# demo-todo-list-api

## About the API

Rest API for to-do list management. It is built using Java, Spring Framework, Spring Boot, Lombok and PostgreSQL for
educational purposes to practice building the rest api. The project runs and runs in docker containers using
docker-compose. The API main URL `/api/v1`.

## Features

This API provides HTTP endpoint's and tools for the following:

* Fetch a to-do lists: `GET/api/v1/todo-lists`
    * Optional request param `optional_prefix_todo_list_name` (if not informed, all to-do lists will be returned)
* Create or Update a to-do list: `PUT/api/v1/todo-lists`
    * Optional request param `todo_list_name`
    * Optional request param `todo_list_id` (if not informed, a new to-do list will be created)
* Delete a to-do list: `DELETE/api/v1/todo-lists/{todo_list_id}`
    * Required path variable `todo_list_id`


* Fetch a tasks in the to-do list: `GET/api/v1/todo-lists/{todo_list_id}/tasks`
    * Required path variable `todo_list_id`
    * Optional request param `optional_prefix_task_title` (if not informed, all tasks will be returned)
* Create a task in the to-do list: `POST/api/v1/todo-lists/{todo_list_id}/tasks`
    * Required path variable `todo_list_id`
    * Required request param `task_title`
    * Optional request param `task_description`
* Update the task: `PATCH/api/v1/tasks/{task_id}`
    * Required path variable `task_id`
    * Optional request param `task_title`
    * Optional request param `task_description`
    * Optional request param `task_is_done` (boolean)
* Change task position in the to-do list: `PATCH/api/v1/tasks/{task_id}/position/change`
    * Required path variable `task_id`
    * Optional request param `new_previous_task_id` (if not informed, the task will be moved to the first position)
* Delete the task: `DELETE/api/v1/tasks/{task_id}`
    * Required path variable `task_id`

### Details

### `PUT/api/v1/todo-lists`
This end-point is called to create or update a to-do list.

#### Create a to-do list:
Example request: `PUT/api/v1/todo-lists?todo_list_name=TestListName`\
Example response:
```json
{
  "id": 22,
  "name": "TestListName",
  "tasks": [],
  "created_at": "2023-06-11T17:16:14.162026800Z",
  "updated_at": "2023-06-11T17:16:14.162026800Z"
}
```

#### Update a to-do list:
Example request: `PUT/api/v1/todo-lists?todo_list_id=22&todo_list_name=TestListNameUpdated`\
Example response:
```json
{
  "id": 22,
  "name": "TestListNameUpdated",
  "tasks": [],
  "created_at": "2023-06-11T17:16:14.162027Z",
  "updated_at": "2023-06-11T17:20:20.598557300Z"
}
```

#### Possible exceptions responses:
* 400 - Bad Request: The request was unacceptable, probably because of an empty or already existing name.
* 404 - Not Found: The requested to-do list not found.


### `GET/api/v1/todo-lists`
This end-point is called to fetch a to-do lists.

#### Fetch all to-do lists:
Example request: `GET/api/v1/todo-lists`\
Example response:
```json
[
  {
    "id": 23,
    "name": "List 1",
    "tasks": [
      {
        "id": 25,
        "title": "Task 1",
        "description": null,
        "is_done": false,
        "todo_list_id": 23,
        "previous_task_id": null,
        "next_task_id": 26,
        "created_at": "2023-06-11T17:51:55.354071Z",
        "updated_at": "2023-06-11T17:51:55.354071Z"
      },
      {
        "id": 26,
        "title": "Task 2",
        "description": null,
        "is_done": false,
        "todo_list_id": 23,
        "previous_task_id": 25,
        "next_task_id": null,
        "created_at": "2023-06-11T17:52:02.298077Z",
        "updated_at": "2023-06-11T17:52:02.298077Z"
      }
    ],
    "created_at": "2023-06-11T17:50:37.950935Z",
    "updated_at": "2023-06-11T17:50:37.950935Z"
  },
  {
    "id": 24,
    "name": "ToDo List",
    "tasks": [],
    "created_at": "2023-06-11T17:50:49.079825Z",
    "updated_at": "2023-06-11T17:50:49.079825Z"
  }
]
```

#### Fetch to-do lists by prefix name:
Example request: `GET/api/v1/todo-lists?optional_prefix_todo_list_name=To`\
Example response:
```json
[
  {
    "id": 24,
    "name": "ToDo List",
    "tasks": [],
    "created_at": "2023-06-11T17:50:49.079825Z",
    "updated_at": "2023-06-11T17:50:49.079825Z"
  }
]
```


### `DELETE/api/v1/todo-lists/{todo_list_id}`
This end-point is called to delete a to-do list.

#### Delete a to-do list:
Example request: `DELETE/api/v1/todo-lists/24`\
Example response:
```json
{
  "answer": true
}
```

#### Possible exceptions responses:
* 404 - Not Found: The requested to-do list not found.


### `POST/api/v1/todo-lists/{todo_list_id}/tasks`
This end-point is called to create a new task in a to-do list.

#### Create a new task in a to-do list:
Example request with description: `POST/api/v1/todo-lists/27/tasks?task_title=Task 1&task_description=text`\
Example response:
```json
{
  "id": 28,
  "title": "Task 1",
  "description": "text",
  "is_done": false,
  "todo_list_id": 27,
  "previous_task_id": null,
  "next_task_id": null,
  "created_at": "2023-06-11T18:07:03.379112Z",
  "updated_at": "2023-06-11T18:07:03.379112Z"
}
```

Example request without description: `POST/api/v1/todo-lists/27/tasks?task_title=Task 2`\
Example response:
```json
{
  "id": 29,
  "title": "Task 2",
  "description": null,
  "is_done": false,
  "todo_list_id": 27,
  "previous_task_id": 28,
  "next_task_id": null,
  "created_at": "2023-06-11T18:10:43.717172300Z",
  "updated_at": "2023-06-11T18:10:43.717172300Z"
}
```

#### Possible exceptions responses:
* 400 - Bad Request: The request was unacceptable, probably because of an empty or already existing title.
* 404 - Not Found: The requested to-do list not found.


### `GET/api/v1/todo-lists/{todo_list_id}/tasks`
This end-point is called to fetch tasks in a to-do list.

#### Fetch tasks in a to-do list:
Example request without prefix title: `GET/api/v1/todo-lists/27/tasks`\
Example response:
```json
[
  {
    "id": 28,
    "title": "Task 1",
    "description": "text",
    "is_done": false,
    "todo_list_id": 27,
    "previous_task_id": null,
    "next_task_id": 29,
    "created_at": "2023-06-11T18:07:03.379112Z",
    "updated_at": "2023-06-11T18:07:03.379112Z"
  },
  {
    "id": 29,
    "title": "Task 2",
    "description": null,
    "is_done": false,
    "todo_list_id": 27,
    "previous_task_id": 28,
    "next_task_id": null,
    "created_at": "2023-06-11T18:10:43.717172Z",
    "updated_at": "2023-06-11T18:10:43.717172Z"
  }
]
```

Example request with prefix title: `GET/api/v1/todo-lists/27/tasks?optional_prefix_task_title=Task 1`\
Example response:
```json
[
  {
    "id": 28,
    "title": "Task 1",
    "description": "text",
    "is_done": false,
    "todo_list_id": 27,
    "previous_task_id": null,
    "next_task_id": 29,
    "created_at": "2023-06-11T18:07:03.379112Z",
    "updated_at": "2023-06-11T18:07:03.379112Z"
  }
]
```

#### Possible exceptions responses:
* 404 - Not Found: The requested to-do list not found.


### `PATCH/api/v1/tasks/{task_id}`
This end-point is called to update a task.

#### Update a task title:
Example request: `PATCH/api/v1/tasks/28?task_title=Task 1 updated`\
Example response:
```json
{
  "id": 28,
  "title": "Task 1 updated",
  "description": "text",
  "is_done": false,
  "todo_list_id": 27,
  "previous_task_id": null,
  "next_task_id": 29,
  "created_at": "2023-06-11T18:07:03.379112Z",
  "updated_at": "2023-06-11T18:23:02.911843800Z"
}
```

#### Update a task all fields:
Example request: `PATCH/api/v1/tasks/28?task_title=Task 1 updated 2&task_description=text updated&is_done=true`\
Example response:
```json
{
  "id": 28,
  "title": "Task 1 updated 2",
  "description": "text updated",
  "is_done": true,
  "todo_list_id": 27,
  "previous_task_id": null,
  "next_task_id": 29,
  "created_at": "2023-06-11T18:07:03.379112Z",
  "updated_at": "2023-06-11T18:24:31.679850Z"
}
```

#### Possible exceptions responses:
* 400 - Bad Request: The request was unacceptable, probably because of an empty or already existing title.
* 404 - Not Found: The requested task not found.


### `PATCH/api/v1/tasks/{task_id}/position/change`
This end-point is called to change a task position.

#### Change a task position to the first:
Example request: `PATCH/api/v1/tasks/28/position/change`\
Example response:
```json
{
  "id": 28,
  "title": "Task 1 updated 2",
  "description": "text updated",
  "is_done": true,
  "todo_list_id": 27,
  "previous_task_id": null,
  "next_task_id": 29,
  "created_at": "2023-06-11T18:07:03.379112Z",
  "updated_at": "2023-06-11T18:24:31.679850Z"
}
```

#### Change a task position:
Example request: `PATCH/api/v1/tasks/28/position/change?new_previous_task_id=29`\
Example response:
```json
{
  "id": 28,
  "title": "Task 1 updated 2",
  "description": "text updated",
  "is_done": true,
  "todo_list_id": 27,
  "previous_task_id": 29,
  "next_task_id": null,
  "created_at": "2023-06-11T18:07:03.379112Z",
  "updated_at": "2023-06-11T18:24:31.679850Z"
}
```

#### Possible exceptions responses:
* 400 - Bad Request: The request was unacceptable, possibly because the task cannot be moved to itself or the new previous task does not belong to the same list.
* 404 - Not Found: The requested task not found.


### `DELETE/api/v1/tasks/{task_id}`
This end-point is called to delete a task.

#### Delete a task:
Example request: `DELETE/api/v1/tasks/28`\
Example response:
```json
{
  "answer": true
}
```

#### Possible exceptions responses:
* 404 - Not Found: The requested task not found.

## Technologies used

This project was developed with:

* **Java 17**
* **Spring Boot 2.7.12**
* **Spring Web**
* **Spring Data JPA**
* **Gradle**
* **PostgreSQL**
* **Lombok**


## Build and Run

The API was designed to run in a docker container, and raise the database with docker-compose.\
To execute the build and run must:

### Build
- Clone the repository
```bash
git clone https://github.com/Danilbel/demo-todo-list-api.git
```

- Enter the project directory
```bash
cd demo-todo-list-api
```

- Make the gradlew file executable
```bash
  chmod +x gradlew
  ```

- Build the project
```bash
./gradlew build
```

After successfully building the project, the `demo-todo-list-api-1.0-SNAPSHOT.jar` file will be generated in the `build/libs` directory.

### Run
- Run the docker-compose
```bash
docker-compose up
```

After successfully running the docker-compose, the API will be available at `http://localhost:9120/api/v1/` and the database at `http://postgresql:5432/`.