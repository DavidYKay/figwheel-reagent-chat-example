(ns chat.core
  (:require [reagent.core :as r]
            [cljs.reader :refer [read-string]]
            [ajax.core :refer [GET POST]]))

;; Setup Code

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:chat-input ""
                            :messages []
                            :username "Anonymous"}))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

;; Network Calls

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn refresh-messages []
  (GET "http://localhost:3000/messages" {:handler (fn [response]
                                                    (swap! app-state assoc :messages (read-string response)))
                                         :error-handler error-handler}))

;; UI Components

(defn username-input []
  [:div
   [:div "Username:"]
   [:input {:placeholder "Neo",
            :type "text"
            :value (:username @app-state)
            :on-change (fn [ev]
                         (swap! app-state assoc :username (-> ev .-target .-value)))}]])


(defn chat-messages []
   [:div "Chat Messages"]
   [:ul
    (for [{:keys [text sender] :as message} (:messages @app-state)]
      [:li (str sender ": " text)])])

(defn chat-input []
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
                           (.preventDefault %))} "Send Message"]])

(defn refresh-button []
   [:button {:class "button"
             :on-click #(do
                          (refresh-messages)
                          (.preventDefault %))} "Refresh"])


;; Main App UI

(defn component-render []
  [:div

   [username-input]

   [chat-messages]

   [chat-input]

   [refresh-button]])

(defn component-did-mount [x]
  (refresh-messages))

(defn main-ui []
  (r/create-class {:reagent-render component-render
                   :component-did-mount component-did-mount}))

;; Entry point

(r/render-component [main-ui]
                    (js/document.getElementById "app"))
