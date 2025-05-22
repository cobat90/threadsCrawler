# 🔍 Web Crawler for Word Search

A simple web crawler that scans all pages within a given base URL and searches for a specific word.

---

## 📌 Features

- Crawls all internal pages of the provided URL  
- Searches for a given word across all discovered pages  
- Provides two HTTP endpoints to interact with the service  
- Uses multi-threading to parallelize page fetching  

---

## 📡 API Endpoints

### 🔸 POST `/crawl`

Sends a word to be searched across the crawled pages.

```json
#### 📥 Request
POST /crawl HTTP/1.1
Host: localhost:4567
Content-Type: application/json
{
  "keyword": "security"
}

📤 Response
HTTP/1.1 200 OK  
Content-Type: application/json
{
  "id": "30vbllyb"
}

🔸 GET /crawl/{id}
Returns a list of all paths where the word was found.

📥 Request
GET /crawl/30vbllyb HTTP/1.1
Host: localhost:4567

📤 Response
HTTP/1.1 200 OK  
Content-Type: application/json
{
  "id": "30vbllyb",
  "status": "active",
  "urls": [
    "http://hiring.axreng.com/index2.html",
    "http://hiring.axreng.com/htmlman1/chcon.1.html"
  ]
}

