{-# LANGUAGE OverloadedStrings #-}

module Euclid.Render.Markdown
    ( renderMarkdown
    ) where

import Data.Text (Text)
import qualified Data.Text as T
import qualified Data.Map.Strict as Map
import Euclid.Model.Types

renderMarkdown :: World -> Text
renderMarkdown world = T.unlines $
    ["# Timeline Summary", ""]
    ++ timelinesSection
    ++ [""]
    ++ entitiesSection
    ++ [""]
    ++ relationshipsSection
  where
    timelinesSection =
        ["## Timelines", "", "| Name | Kind | Start | End |", "| --- | --- | --- | --- |"]
        ++ [ "| " <> timelineName tl
            <> " | " <> kindText (timelineKind tl)
            <> " | " <> showT (timePointOrdinal (timelineStart tl))
            <> " | " <> showT (timePointOrdinal (timelineEnd tl))
            <> " |"
           | tl <- Map.elems (worldTimelines world)
           ]
    entitiesSection =
        ["## Entities", "", "| Name | Type | Appearances |", "| --- | --- | --- |"]
        ++ [ "| " <> entityName e
            <> " | " <> entityType e
            <> " | " <> T.intercalate "; " (map renderApp (entityAppearances e))
            <> " |"
           | e <- Map.elems (worldEntities world)
           ]
    relationshipsSection =
        ["## Relationships", "", "| Source | Label | Target |", "| --- | --- | --- |"]
        ++ [ "| " <> relSource r
            <> " | " <> maybe "-" id (relLabel r)
            <> " | " <> relTarget r
            <> " |"
           | r <- worldRelationships world
           ]
    renderApp a =
        appearanceTimeline a <> " @ " <> showT (timePointOrdinal (rangeStart (appearanceRange a)))
            <> ".." <> showT (timePointOrdinal (rangeEnd (appearanceRange a)))

kindText :: TimelineKind -> Text
kindText TimelineLinear = "linear"
kindText TimelineBranch = "branch"
kindText TimelineParallel = "parallel"
kindText TimelineLoop = "loop"

showT :: Show a => a -> Text
showT = T.pack . show
