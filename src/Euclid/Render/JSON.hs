{-# LANGUAGE OverloadedStrings #-}

module Euclid.Render.JSON
    ( renderJson
    ) where

import Data.Text (Text)
import qualified Data.Text as T
import qualified Data.Map.Strict as Map
import Euclid.Model.Types

renderJson :: World -> Text
renderJson world = T.unlines
    [ "{"
    , "  \"timelines\": [" <> T.intercalate ",\n    " (map renderTimeline (Map.elems (worldTimelines world))) <> "],"
    , "  \"entities\": [" <> T.intercalate ",\n    " (map renderEntity (Map.elems (worldEntities world))) <> "],"
    , "  \"relationships\": [" <> T.intercalate ",\n    " (map renderRel (worldRelationships world)) <> "]"
    , "}"
    ]

renderTimeline :: Timeline -> Text
renderTimeline tl = T.concat
    [ "{\"name\":\"" <> esc (timelineName tl) <> "\""
    , ",\"kind\":\"" <> kindText (timelineKind tl) <> "\""
    , ",\"start\":" <> showT (timePointOrdinal (timelineStart tl))
    , ",\"end\":" <> showT (timePointOrdinal (timelineEnd tl))
    , maybe "" (\p -> ",\"parent\":\"" <> esc p <> "\"") (timelineParent tl)
    , "}"
    ]

renderEntity :: Entity -> Text
renderEntity e = T.concat
    [ "{\"name\":\"" <> esc (entityName e) <> "\""
    , ",\"type\":\"" <> esc (entityType e) <> "\""
    , ",\"fields\":{" <> T.intercalate "," [renderField k v | (k, v) <- Map.toList (entityFields e)] <> "}"
    , ",\"appearances\":[" <> T.intercalate "," (map renderAppearance (entityAppearances e)) <> "]"
    , maybe "" (\n -> ",\"note\":\"" <> esc n <> "\"") (annotationNote (entityAnnotation e))
    , maybe "" (\s -> ",\"source\":\"" <> esc s <> "\"") (annotationSource (entityAnnotation e))
    , maybe "" (\c -> ",\"confidence\":" <> showT c) (annotationConfidence (entityAnnotation e))
    , "}"
    ]

renderField :: Text -> Value -> Text
renderField k v = "\"" <> esc k <> "\":" <> renderValue v

renderValue :: Value -> Text
renderValue VNull = "null"
renderValue (VString t) = "\"" <> esc t <> "\""
renderValue (VInt n) = showT n
renderValue (VBool b) = if b then "true" else "false"
renderValue (VDate d) = "\"" <> showT d <> "\""
renderValue (VDuration y m d) = "\"" <> showT y <> "y" <> showT m <> "m" <> showT d <> "d\""
renderValue (VList vs) = "[" <> T.intercalate "," (map renderValue vs) <> "]"
renderValue (VEntityRef r) = "\"" <> esc r <> "\""
renderValue (VTimelineRef r) = "\"" <> esc r <> "\""
renderValue (VClosureRef _) = "\"<closure>\""

renderAppearance :: Appearance -> Text
renderAppearance a = T.concat
    [ "{\"timeline\":\"" <> esc (appearanceTimeline a) <> "\""
    , ",\"start\":" <> showT (timePointOrdinal (rangeStart (appearanceRange a)))
    , ",\"end\":" <> showT (timePointOrdinal (rangeEnd (appearanceRange a)))
    , "}"
    ]

renderRel :: Relationship -> Text
renderRel r = T.concat
    [ "{\"source\":\"" <> esc (relSource r) <> "\""
    , ",\"target\":\"" <> esc (relTarget r) <> "\""
    , maybe "" (\l -> ",\"label\":\"" <> esc l <> "\"") (relLabel r)
    , ",\"directed\":" <> if relDirected r then "true" else "false"
    , ",\"causal\":\"" <> causalText (relCausalKind r) <> "\""
    , "}"
    ]

kindText :: TimelineKind -> Text
kindText TimelineLinear = "linear"
kindText TimelineBranch = "branch"
kindText TimelineParallel = "parallel"
kindText TimelineLoop = "loop"

causalText :: CausalKind -> Text
causalText CausalNone = "none"
causalText CausalCauses = "causes"
causalText CausalEnables = "enables"

showT :: Show a => a -> Text
showT = T.pack . show

esc :: Text -> Text
esc = T.concatMap (\c -> case c of {'"' -> "\\\""; '\\' -> "\\\\"; '\n' -> "\\n"; _ -> T.singleton c})
