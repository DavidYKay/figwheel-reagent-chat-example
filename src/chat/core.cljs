(ns chat.core
  (:require [reagent.core :as r]))

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

(defn main-ui []
  [:div

   [:div "Username:"]
   [:input
     {:placeholder "Neo",
      :type "text"
      :value (:username @app-state)
      :on-change (fn [ev]
                   (swap! app-state assoc :username (-> ev .-target .-value)))}]

   [:div "Chat Messages"]
   [:ul
    (for [{:keys [text sender] :as message} (:messages @app-state)]
      [:li (str sender ": " text)])]

   [:form
    [:input
     {:placeholder "My message here...",
      :type "text"
      :value (:chat-input @app-state)
      :on-change (fn [ev]
                   (swap! app-state assoc :chat-input (-> ev .-target .-value)))}]

    [:button {:class "button"
              :on-click #(do
                           (swap! app-state assoc :messages (conj (:messages @app-state)
                                                                  {:sender (:username @app-state)
                                                                   :text (:chat-input @app-state)}))
                           (swap! app-state dissoc :chat-input)
                           (.preventDefault %))} "Send Message"]]

   ])

(r/render-component [main-ui]
                    (js/document.getElementById "app"))
