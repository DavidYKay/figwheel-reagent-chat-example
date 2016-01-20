(ns chat.core
  (:require [reagent.core :as r]
            [cljs.reader :refer [read-string]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:chat-input ""
                            :messages []
                            :username "Anonymous"}))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)


(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn refresh-messages []
  (GET "http://localhost:3000/messages" {:handler (fn [response]
                                                    (swap! app-state assoc :messages (read-string response)))
                                         :error-handler error-handler}))

(defn component-render []
  [:div

   [:div "Username:"]
   [:input {:placeholder "Neo",
            :type "text"
            :value (:username @app-state)
            :on-change (fn [ev]
                         (swap! app-state assoc :username (-> ev .-target .-value)))}]

   [:div "Chat Messages"]
   [:ul
    (for [{:keys [text sender] :as message} (:messages @app-state)]
      [:li (str sender ": " text)])]

   [:form
    [:input {:placeholder "My message here...",
             :type "text"
             :value (:chat-input @app-state)
             :on-change (fn [ev]
                          (swap! app-state assoc :chat-input (-> ev .-target .-value)))}]

    [:button {:class "button"
              :on-click #(do
                           (POST "http://localhost:3000/messages" {:body (pr-str {:sender (:username @app-state)
                                                                                  :text (:chat-input @app-state)})
                                                                   :handler (fn [response]
                                                                              (let [messages (read-string response)]
                                                                                (swap! app-state (fn [old]
                                                                                                   (-> old
                                                                                                     (dissoc :chat-input)
                                                                                                     (assoc :messages messages))))))
                                                                   :error-handler error-handler})
                           (.preventDefault %))} "Send Message"]]

   [:button {:class "button"
             :on-click #(do
                          (refresh-messages)
                          (.preventDefault %))} "Refresh"]])

(defn component-did-mount [x]
  (refresh-messages))

(defn main-ui []
  (r/create-class {:reagent-render component-render
                   :component-did-mount component-did-mount}))

(r/render-component [main-ui]
                    (js/document.getElementById "app"))
