{-# LANGUAGE OverloadedStrings #-}

module Euclid.Render.Mermaid
    ( renderMermaid
    ) where

import Data.Text (Text)
import qualified Data.Text as T
import Euclid.Render.Layout

renderMermaid :: Layout -> Text
renderMermaid layout = T.unlines $
    [ "gantt"
    , "    title Euclid Timeline"
    , "    dateFormat X"
    , "    axisFormat %s"
    ]
    ++ concatMap renderSection (layoutTimelines layout)
  where
    renderSection tl =
        ("    section " <> layoutTimelineName tl)
            : [ "    "
                <> layoutEntityName e
                <> " :"
                <> (if layoutEntityType e == "event" then "milestone, " else "")
                <> showT (layoutEntityStart e)
                <> ", "
                <> showT (layoutEntityEnd e)
              | e <- layoutEntities layout
              , layoutEntityTimeline e == layoutTimelineName tl
              ]
    showT :: Show a => a -> Text
    showT = T.pack . show
