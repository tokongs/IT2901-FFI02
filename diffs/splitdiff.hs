module SplitDiff where

import Data.List.Split
import System.Directory (createDirectoryIfMissing)
import System.FilePath.Posix (takeDirectory)
import qualified Control.Monad.Parallel as P

isDiff :: String -> Bool
isDiff line = take 10 line == "diff --git"

files :: String -> [String]
files = (unlines <$>) . filter (\l -> length l > 2) . splitWhen isDiff . lines

createAndWriteFile :: FilePath -> String -> IO ()
createAndWriteFile path content = do
  createDirectoryIfMissing True $ takeDirectory path
  writeFile path content

namedFile :: String -> (String, String)
namedFile file = (name, load)
  where
    name = drop 6 $ loc !! 2
    load = unlines loc
    loc  = lines file

writeNamedFile :: (String, String) -> IO ()
writeNamedFile (n, f) = createAndWriteFile n f

writeFiles :: String -> IO ()
---writeFiles str = readFile str >>= mapM_ (writeNamedFile . namedFile <$>) . files
writeFiles str = do
  diff <- readFile str
  mapM_ writeNamedFile (namedFile <$> files diff)
