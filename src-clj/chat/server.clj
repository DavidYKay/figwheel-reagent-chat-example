(ns chat.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(def messages (atom [{:text "Example Message" :sender "David"}]))

(defn wrap-cors-response
  "Middleware that adds a CORS header to the response"
  [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Access-Control-Allow-Origin"] "*"))))

(defroutes app
  (GET "/" [] "<h1>Hello Chat World</h1>")
  (GET "/messages" []
       (pr-str @messages))
  (POST "/messages" {:keys [body] :as request}
        (let [parsed (read-string (slurp body))]
          (swap! messages conj parsed)
          (pr-str @messages)))
  (route/not-found "<h1>Page not found</h1>"))

(def handler (wrap-cors-response app))
