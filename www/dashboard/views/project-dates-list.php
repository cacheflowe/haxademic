<div class="mdl-grid portfolio-max-width">
  <h2 class='mdl-cell mdl-cell-full-width'><?php echo $_projectName ?> Checkins</h2>
</div>
<div class="mdl-grid portfolio-max-width">
  <?php
  // checkins list
  $path = "./projects/$_projectId/checkins";

  // get date directories and sort list
  $datePaths = get_files_chrono($path, true);
  foreach($datePaths as $datePath) {
    ?>
    <div class="mdl-cell mdl-card mdl-shadow--4dp portfolio-card">
      <!-- <div class="mdl-card__media">
        <img class="article-image" src="images/example-work01.jpg" alt="" border="0">
      </div> -->
      <div class="mdl-card__title">
        <h2 class="mdl-card__title-text"><?php echo $datePath ?></h2>
      </div>
      <!-- <div class="mdl-card__supporting-text">
        Enim labore aliqua consequat ut quis ad occaecat aliquip incididunt. Sunt nulla eu enim irure enim nostrud aliqua consectetur ad consectetur sunt ullamco officia. Ex officia laborum et consequat duis.
      </div> -->
      <div class="mdl-card__actions mdl-card--border">
        <a class="mdl-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" href="./?project=<?php echo $_projectId ?>&date=<?php echo $datePath ?>">Go<span class="mdl-button__ripple-container"><span class="mdl-ripple"></span></span></a>
      </div>
    </div>
    <?php
  }
  ?>
</div>
