import React, { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Chip from '@material-ui/core/Chip';
import { makeStyles } from '@material-ui/core/styles';
import axios from '../../axios/axios';

const useStyles = makeStyles((theme) => ({
  container: {
    padding: '40px',
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  },
  header: {
    textAlign: 'center',
    marginBottom: '40px',
    color: '#fff',
  },
  title: {
    fontSize: '3.6rem',
    fontWeight: 'bold',
    marginBottom: '10px',
    fontFamily: 'JalnanOTF',
  },
  subtitle: {
    fontSize: '1.8rem',
    opacity: 0.9,
  },
  categoryFilter: {
    display: 'flex',
    justifyContent: 'center',
    gap: '10px',
    marginBottom: '30px',
    flexWrap: 'wrap',
  },
  categoryChip: {
    fontSize: '1.4rem',
    padding: '8px 16px',
    cursor: 'pointer',
    '&:hover': {
      transform: 'scale(1.05)',
    },
  },
  activeChip: {
    background: '#fff',
    color: '#667eea',
  },
  themeCard: {
    cursor: 'pointer',
    transition: 'all 0.3s ease',
    borderRadius: '16px',
    overflow: 'hidden',
    height: '100%',
    '&:hover': {
      transform: 'translateY(-8px)',
      boxShadow: '0 20px 40px rgba(0,0,0,0.2)',
    },
  },
  cardContent: {
    padding: '24px',
  },
  themeName: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: '8px',
    color: '#333',
  },
  themeNameKo: {
    fontSize: '1.6rem',
    color: '#667eea',
    marginBottom: '12px',
  },
  description: {
    fontSize: '1.4rem',
    color: '#666',
    marginBottom: '16px',
    lineHeight: 1.6,
  },
  metaInfo: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  difficultyChip: {
    fontSize: '1.2rem',
  },
  categoryTag: {
    fontSize: '1.2rem',
    background: '#f0f0f0',
  },
  backButton: {
    position: 'absolute',
    top: '20px',
    left: '20px',
    background: 'rgba(255,255,255,0.2)',
    color: '#fff',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '1.4rem',
    '&:hover': {
      background: 'rgba(255,255,255,0.3)',
    },
  },
}));

const getDifficultyColor = (level) => {
  const colors = {
    1: '#4caf50',
    2: '#8bc34a',
    3: '#ffc107',
    4: '#ff9800',
    5: '#f44336',
  };
  return colors[level] || '#9e9e9e';
};

const getDifficultyLabel = (level) => {
  const labels = {
    1: 'Beginner',
    2: 'Elementary',
    3: 'Intermediate',
    4: 'Advanced',
    5: 'Expert',
  };
  return labels[level] || 'Unknown';
};

function ClozeThemeList() {
  const classes = useStyles();
  const history = useHistory();
  const [themes, setThemes] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchThemes();
    fetchCategories();
  }, []);

  const fetchThemes = async (category = null) => {
    try {
      setLoading(true);
      let url = '/api/cloze/themes';
      if (category && category !== 'All') {
        url = `/api/cloze/themes/category/${category}`;
      }
      const response = await axios.get(url);
      setThemes(response.data.data || []);
    } catch (error) {
      console.error('Failed to fetch themes:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await axios.get('/api/cloze/categories');
      setCategories(['All', ...(response.data.data || [])]);
    } catch (error) {
      console.error('Failed to fetch categories:', error);
    }
  };

  const handleCategoryClick = (category) => {
    setSelectedCategory(category);
    fetchThemes(category);
  };

  const handleThemeClick = (themeId) => {
    history.push(`/cloze/theme/${themeId}`);
  };

  const handleBack = () => {
    history.push('/');
  };

  return (
    <div className={classes.container}>
      <button className={classes.backButton} onClick={handleBack}>
        ← Back
      </button>

      <div className={classes.header}>
        <Typography className={classes.title}>Cloze Learning</Typography>
        <Typography className={classes.subtitle}>
          주제별 문맥 속에서 단어를 학습하세요
        </Typography>
      </div>

      <div className={classes.categoryFilter}>
        {categories.map((category) => (
          <Chip
            key={category}
            label={category}
            className={`${classes.categoryChip} ${
              selectedCategory === category ? classes.activeChip : ''
            }`}
            onClick={() => handleCategoryClick(category)}
            variant={selectedCategory === category ? 'default' : 'outlined'}
            style={{
              color: selectedCategory === category ? '#667eea' : '#fff',
              borderColor: '#fff',
            }}
          />
        ))}
      </div>

      <Grid container spacing={3}>
        {themes.map((theme) => (
          <Grid item xs={12} sm={6} md={4} key={theme.themeId}>
            <Card
              className={classes.themeCard}
              onClick={() => handleThemeClick(theme.themeId)}
            >
              <CardContent className={classes.cardContent}>
                <Typography className={classes.themeName}>
                  {theme.themeName}
                </Typography>
                <Typography className={classes.themeNameKo}>
                  {theme.themeNameKo}
                </Typography>
                <Typography className={classes.description}>
                  {theme.description}
                </Typography>
                <div className={classes.metaInfo}>
                  <Chip
                    label={getDifficultyLabel(theme.difficultyLevel)}
                    className={classes.difficultyChip}
                    style={{
                      background: getDifficultyColor(theme.difficultyLevel),
                      color: '#fff',
                    }}
                    size="small"
                  />
                  <Chip
                    label={theme.category}
                    className={classes.categoryTag}
                    size="small"
                  />
                </div>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {loading && (
        <Typography style={{ textAlign: 'center', color: '#fff', marginTop: '40px' }}>
          Loading...
        </Typography>
      )}

      {!loading && themes.length === 0 && (
        <Typography style={{ textAlign: 'center', color: '#fff', marginTop: '40px' }}>
          No themes available.
        </Typography>
      )}
    </div>
  );
}

export default ClozeThemeList;
