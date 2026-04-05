{-# LANGUAGE OverloadedStrings #-}

module Euclid.Model.Types
    ( Appearance(..)
    , BinaryOp(..)
    , UnaryOp(..)
    , Diagnostic(..)
    , DiagnosticLevel(..)
    , Constraint(..)
    , Entity(..)
    , StateChange(..)
    , entityFieldAt
    , FunctionSig(..)
    , Relationship(..)
    , TimePoint(..)
    , TimeRange(..)
    , Timeline(..)
    , TimelineKind(..)
    , TypeDef(..)
    , TypeField(..)
    , Value(..)
    , World(..)
    , builtInTypes
    , emptyWorld
    , findEntity
    , findTimeline
    , noSourceSpan
    , renderDiagnostic
    , SourceSpan(..)
    , timePointFromValue
    , timePointOrdinal
    , addDurationToDay
    , daysBetween
    ) where

import qualified Data.List
import Data.List (intercalate)
import Data.Map.Strict (Map)
import qualified Data.Map.Strict as Map
import Data.Set (Set)
import qualified Data.Set as Set
import Data.Text (Text)
import qualified Data.Text as T
import Data.Time (Day, toModifiedJulianDay, addGregorianMonthsClip, addDays, diffDays)

data Value
    = VNull
    | VString Text
    | VInt Integer
    | VBool Bool
    | VDate Day
    | VDuration Integer Integer Integer -- years months days
    | VList [Value]
    | VEntityRef Text
    | VTimelineRef Text
    | VClosureRef Integer
    deriving (Eq, Ord, Show)

data BinaryOp
    = OpAdd
    | OpSub
    | OpMul
    | OpDiv
    | OpMod
    | OpConcat
    | OpGt
    | OpLt
    | OpGte
    | OpLte
    | OpEq
    | OpNeq
    | OpAnd
    | OpOr
    deriving (Eq, Ord, Show)

data UnaryOp
    = OpNeg
    | OpNot
    deriving (Eq, Ord, Show)

data TimelineKind
    = TimelineLinear
    | TimelineBranch
    | TimelineParallel
    | TimelineLoop
    deriving (Eq, Ord, Show)

data TimePoint
    = TimeDate Day
    | TimeOrdinal Integer
    deriving (Eq, Ord, Show)

data TimeRange = TimeRange
    { rangeStart :: TimePoint
    , rangeEnd :: TimePoint
    }
    deriving (Eq, Ord, Show)

data TypeField = TypeField
    { typeFieldName :: Text
    , typeFieldType :: Text
    , typeFieldOptional :: Bool
    }
    deriving (Eq, Show)

data SourceSpan = SourceSpan
    { spanFile :: FilePath
    , spanStartLine :: Int
    , spanStartColumn :: Int
    , spanEndLine :: Int
    , spanEndColumn :: Int
    }
    deriving (Eq, Ord, Show)

data TypeDef = TypeDef
    { typeName :: Text
    , typeParent :: Maybe Text
    , typeFields :: [TypeField]
    , typeMeta :: Map Text Value
    , typeSourceSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

data Timeline = Timeline
    { timelineName :: Text
    , timelineKind :: TimelineKind
    , timelineStart :: TimePoint
    , timelineEnd :: TimePoint
    , timelineParent :: Maybe Text
    , timelineForkFrom :: Maybe (Text, TimePoint)
    , timelineMergeInto :: Maybe (Text, TimePoint)
    , timelineLoopCount :: Maybe Integer
    , timelineSourceSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

data Appearance = Appearance
    { appearanceTimeline :: Text
    , appearanceRange :: TimeRange
    }
    deriving (Eq, Show)

data StateChange = StateChange
    { stateChangeTime :: TimePoint
    , stateChangeFields :: Map Text Value
    }
    deriving (Eq, Show)

data Entity = Entity
    { entityName :: Text
    , entityType :: Text
    , entityFields :: Map Text Value
    , entityAppearances :: [Appearance]
    , entityStateChanges :: [StateChange]
    , entitySourceSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

entityFieldAt :: Entity -> Text -> TimePoint -> Maybe Value
entityFieldAt entity field tp =
    let applicable = filter (\sc -> timePointOrdinal (stateChangeTime sc) <= timePointOrdinal tp) (entityStateChanges entity)
        sorted = Data.List.sortOn (negate . timePointOrdinal . stateChangeTime) applicable
        fromChanges = Data.List.find (\sc -> Map.member field (stateChangeFields sc)) sorted >>= Map.lookup field . stateChangeFields
    in case fromChanges of
        Just v -> Just v
        Nothing -> Map.lookup field (entityFields entity)

data Relationship = Relationship
    { relSource :: Text
    , relLabel :: Maybe Text
    , relTarget :: Text
    , relDirected :: Bool
    , relTemporalScope :: Maybe TimeRange
    , relSourceSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

data FunctionSig = FunctionSig
    { functionName :: Text
    , functionParams :: [(Text, Text)]
    , functionReturnType :: Maybe Text
    , functionSourceSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

data DiagnosticLevel
    = DiagnosticError
    | DiagnosticWarning
    deriving (Eq, Ord, Show)

data Diagnostic = Diagnostic
    { diagnosticLevel :: DiagnosticLevel
    , diagnosticSource :: Text
    , diagnosticMessage :: Text
    , diagnosticSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

data Constraint = Constraint
    { constraintName :: Text
    , constraintSourceSpan :: Maybe SourceSpan
    }
    deriving (Eq, Show)

data World = World
    { worldTypes :: Map Text TypeDef
    , worldTimelines :: Map Text Timeline
    , worldEntities :: Map Text Entity
    , worldRelationships :: [Relationship]
    , worldFunctions :: Map Text FunctionSig
    , worldConstraints :: [Constraint]
    }
    deriving (Eq, Show)

emptyWorld :: World
emptyWorld =
    World
        { worldTypes = Map.empty
        , worldTimelines = Map.empty
        , worldEntities = Map.empty
        , worldRelationships = []
        , worldFunctions = Map.empty
        , worldConstraints = []
        }

builtInTypes :: Set Text
builtInTypes =
    Set.fromList
        [ "entity"
        , "event"
        , "person"
        , "place"
        , "object"
        , "group"
        , "character"
        , "artifact"
        , "location"
        , "faction"
        ]

noSourceSpan :: SourceSpan
noSourceSpan =
    SourceSpan
        { spanFile = "<unknown>"
        , spanStartLine = 1
        , spanStartColumn = 1
        , spanEndLine = 1
        , spanEndColumn = 1
        }

findTimeline :: Text -> World -> Maybe Timeline
findTimeline name = Map.lookup name . worldTimelines

findEntity :: Text -> World -> Maybe Entity
findEntity name = Map.lookup name . worldEntities

timePointOrdinal :: TimePoint -> Integer
timePointOrdinal (TimeDate day) = toModifiedJulianDay day
timePointOrdinal (TimeOrdinal value) = value

timePointFromValue :: Value -> Either Text TimePoint
timePointFromValue (VDate day) = Right (TimeDate day)
timePointFromValue (VInt value) = Right (TimeOrdinal value)
timePointFromValue value =
    Left $
        "expected a date or integer time point, got " <> T.pack (show value)

renderDiagnostic :: Diagnostic -> Text
renderDiagnostic diagnostic =
    T.intercalate
        " "
        [ levelLabel (diagnosticLevel diagnostic) <> ":"
        , diagnosticSource diagnostic <> "-"
        , maybe "" ((<> "-") . renderSourceSpan) (diagnosticSpan diagnostic)
        , diagnosticMessage diagnostic
        ]
  where
    levelLabel DiagnosticError = "error"
    levelLabel DiagnosticWarning = "warning"

renderSourceSpan :: SourceSpan -> Text
renderSourceSpan sourceSpan =
    T.pack (spanFile sourceSpan)
        <> ":"
        <> T.pack (show (spanStartLine sourceSpan))
        <> ":"
        <> T.pack (show (spanStartColumn sourceSpan))

addDurationToDay :: Day -> Integer -> Integer -> Integer -> Day
addDurationToDay day years months days =
    addDays days $ addGregorianMonthsClip (years * 12 + months) day

daysBetween :: Day -> Day -> Integer
daysBetween = diffDays

_unusedTextHelper :: [Text] -> Text
_unusedTextHelper = T.pack . intercalate ", " . map T.unpack
